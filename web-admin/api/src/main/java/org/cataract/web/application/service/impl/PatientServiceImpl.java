package org.cataract.web.application.service.impl;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.ImageService;
import org.cataract.web.application.service.ReportsService;
import org.cataract.web.application.service.PatientService;
import org.cataract.web.domain.*;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.domain.exception.PatientNotFoundException;
import org.cataract.web.infra.InstitutionRepository;
import org.cataract.web.infra.PatientProfileRepository;
import org.cataract.web.infra.PatientRepository;
import org.cataract.web.infra.ReportsRepository;
import org.cataract.web.presentation.dto.requests.CreatePatientRequestDto;
import org.cataract.web.presentation.dto.requests.PatientListRequestDto;
import org.cataract.web.presentation.dto.requests.ReportsListRequestDto;
import org.cataract.web.presentation.dto.requests.UpdatePatientRequestDto;
import org.cataract.web.presentation.dto.responses.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final ReportsRepository reportsRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final InstitutionRepository institutionRepository;
    private final ReportsService reportsService;
    private final ImageService imageService;

    @Transactional
    public PatientResponseDto createPatient(Institution institution, CreatePatientRequestDto createPatientRequestDto) {

        Optional<Integer> patientId = patientRepository.findMaxByInstitutionOrderByPatientIdDesc(institution);

        Patient patient = new Patient(createPatientRequestDto, institution, patientId.orElse(0) + 1);

        patient = patientRepository.save(patient);
        log.debug("[{}] patient {} saved", institution.getName(), patient.getName());
        createPatientProfile(createPatientRequestDto, patient);
        log.debug("[{}] patient {} profile saved", institution.getName(), patient.getName());
        return PatientResponseDto.toDto(patient);
    }


    @Transactional
    public void createPatientProfile(CreatePatientRequestDto createPatientRequestDto, Patient patient) {
        PatientProfiles patientProfiles = new PatientProfiles(createPatientRequestDto, patient);
        patientProfileRepository.save(patientProfiles);

    }


    @Transactional
    public PatientResponseDto updatePatient(Institution institution, Integer patientId, UpdatePatientRequestDto updatePatientRequestDto) {
;

        Patient existingPatient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);
        if (updatePatientRequestDto.getPatientName() != null)
            existingPatient.setName(updatePatientRequestDto.getPatientName());
        if (updatePatientRequestDto.getSex() != null)
            existingPatient.setSex(updatePatientRequestDto.getSex());
        try {
            existingPatient.setDateOfBirth(DateFormatHelper.string2Date(updatePatientRequestDto.getDateOfBirth()));
        } catch (Exception e) {
            log.error("failed to update Patient {}", updatePatientRequestDto, e);
        }
        existingPatient.setPhoneNum(updatePatientRequestDto.getPhoneNum());
        log.debug("[{}] patient {} updated", institution.getName(), existingPatient.getName());
        return PatientResponseDto.toDto(patientRepository.save(existingPatient));
    }

    @Transactional
    public void deletePatient(Institution institution, Integer patientId) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);
        patient.setDataStatus(0);
        log.debug("[{}] patient {} deleted", institution.getName(), patient.getName());
        patientRepository.save(patient);
    }

    @Transactional
    public void deletePatientAndDataPermanently(Institution institution, Integer patientId) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 0)
                .orElseThrow(PatientNotFoundException::new);
        Optional<PatientProfiles> patientProfiles = patientProfileRepository.findByPatient(patient);
        patientProfiles.ifPresent(patientProfileRepository::delete);
        log.debug("[{}] patient {} deleted profile permanently", institution.getName(), patient.getName());
        List<Report> patientReports =
                (List<Report>) reportsService.getReportsByPatient(institution, patient.getPatientId(),
                        new ReportsListRequestDto(), Pageable.unpaged());
        for (Report report: patientReports) {
            imageService.deleteFile(report.getLImagePath(), institution.getImageStorage());
            imageService.deleteFile(report.getRImagePath(), institution.getImageStorage());
        }
        reportsRepository.deleteAllInBatch(patientReports);
        log.debug("[{}] patient {} deleted reports permanently", institution.getName(), patient.getName());
        patientRepository.delete(patient);
        log.debug("[{}] patient data {} deleted permanently", institution.getName(), patient.getName());
    }

    @Transactional
    public int deletePatientByPolicy(int retentionDays){

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime baseDateTime = now.minusDays(retentionDays);

        List<Patient> deletePatientList = patientRepository.findByDataStatusEqualsAndUpdatedAtBefore(0, baseDateTime);
        List<Report> patientReportList = new ArrayList<>();
        for (Patient patient : deletePatientList) {
            patientProfileRepository.findByPatient(patient).ifPresent(patientProfileRepository::delete);
            patientReportList.addAll((List<Report>) reportsService.getReportsByPatient(patient.getInstitution(), patient.getPatientId(),
                    new ReportsListRequestDto(), Pageable.unpaged()));
        }

        log.debug("{} patients deleted profile permanently as per retention policy", deletePatientList.size());
        for (Report report: patientReportList) {
            imageService.deleteFile(report.getLImagePath(), report.getInstitution().getImageStorage());
            imageService.deleteFile(report.getRImagePath(), report.getInstitution().getImageStorage());
        }
        int deleteReports = deletePatientList.size();
        reportsRepository.deleteAllInBatch(patientReportList);
        log.debug("{} patient reports deleted reports permanently as per retention policy", deleteReports);
        patientRepository.deleteAllInBatch(deletePatientList);
        return deleteReports;

    }

    public Object getPatientsByInstitution(List<Institution> institutionList, PatientListRequestDto patientListRequestDto, Pageable pageable) {

        Specification<Patient> spec = getJpaSpecFromRequest(institutionList, patientListRequestDto);
        log.debug("[{}] retrieve patients list with dto {}", Arrays.toString(institutionList.toArray()), patientListRequestDto.toString());
        if (pageable.isPaged()) {
            Sort.Direction direction = Sort.Direction.fromString(patientListRequestDto.getSortDir());
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, patientListRequestDto.getSortBy()));
            Page<Patient> patientsList = patientRepository.findAll(spec, pageable);
            return patientsList.map(PatientResponseDto::toDto);
        } else {
            Sort sort = Sort.by( Sort.Direction.fromString(patientListRequestDto.getSortDir()), patientListRequestDto.getSortBy());
            List<Patient> patientList = patientRepository.findAll(spec, sort);
            return patientList.stream().map(PatientResponseDto::toDto).collect(Collectors.toList());
        }
    }


    @Transactional
    public FullPatientDataDto getFullPatientData(Institution institution, Integer patientId, int numOfReports) {
;

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        Optional<PatientProfiles> optionalPatientProfiles = patientProfileRepository.findByPatient(patient);

        PatientProfileResponseDto patientProfileResponseDto = new PatientProfileResponseDto();
        if (optionalPatientProfiles.isPresent())
            patientProfileResponseDto = PatientProfileResponseDto.toDto(optionalPatientProfiles.get());

        List<PatientReportResponseDto> patientReports =
                reportsService.getRecentReportByPatientIdAndInstitution(institution, patientId, numOfReports);

        if (patientReports.isEmpty())
            patientReports.add(new PatientReportResponseDto());
        return FullPatientDataDto.toDto(patient, patientProfileResponseDto, patientReports);
    }

    @Transactional
    public PatientResponseDto getPatient(Institution institution, Integer patientId) {
;

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        return PatientResponseDto.toDto(patient);
    }

    private List<PatientExportResponseDto> getPatientsListByUserInstitution(Institution institution, PatientListRequestDto patientListRequestDto) {
;

        Specification<Patient> spec = getJpaSpecFromRequest(List.of(institution), patientListRequestDto);

        List<Patient> patientList = patientRepository.findAll(spec);

        return patientList.stream().map(PatientExportResponseDto::toDto).toList();

    }

    @Transactional
    public ByteArrayInputStream getPatientsListByUserInstitutionAsCsv(Institution institution, PatientListRequestDto patientListRequestDto) {

        List<PatientExportResponseDto> data = getPatientsListByUserInstitution(institution, patientListRequestDto);
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(PatientExportResponseDto.class).withHeader();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            csvMapper.writer(schema).writeValue(out, data);
        } catch (IOException e) {
            log.error("failed to create csv file", e);
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    @Transactional
    public PatientResponseDto restorePatient(Institution institution, Integer patientId) {
        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusEquals(patientId, institution, 0)
                .orElseThrow(PatientNotFoundException::new);
        patient.setDataStatus(1);
        patientRepository.save(patient);
        return PatientResponseDto.toDto(patient);
    }

    private Specification<Patient> getJpaSpecFromRequest(List<Institution> institutionList, PatientListRequestDto patientListRequestDto) {
        Specification<Patient> spec = Specification.where(null);

        if (institutionList != null && !institutionList.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.get("institution").in(institutionList)
            );
        }


        if (Objects.nonNull(patientListRequestDto.getStartDate())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), patientListRequestDto.getStartDate())
            );
        }

        if (Objects.nonNull(patientListRequestDto.getEndDate())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), patientListRequestDto.getEndDate().atTime(23, 59, 59))
            );
        }

        if (Objects.nonNull(patientListRequestDto.getQuery()) && !patientListRequestDto.getQuery().isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + patientListRequestDto.getQuery().toLowerCase() + "%")
            );
        }

        if (Objects.nonNull(patientListRequestDto.getSex()) && !patientListRequestDto.getSex().isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("sex"), patientListRequestDto.getSex())
            );
        }

        if (Objects.nonNull(patientListRequestDto.getDateOfBirthFrom())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("dateOfBirth"), patientListRequestDto.getDateOfBirthFrom())
            );
        }

        if (Objects.nonNull(patientListRequestDto.getDateOfBirthTo())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), patientListRequestDto.getDateOfBirthTo())
            );
        }

        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("dataStatus"), patientListRequestDto.getDataStatus())
        );

        return spec;

    }
}

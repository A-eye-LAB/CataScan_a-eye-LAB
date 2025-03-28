package org.cataract.web.application.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.ImageService;
import org.cataract.web.application.service.ReportsService;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.Patient;
import org.cataract.web.domain.Reports;
import org.cataract.web.domain.exception.PatientNotFoundException;
import org.cataract.web.domain.exception.ReportNotFoundException;
import org.cataract.web.infra.PatientRepository;
import org.cataract.web.infra.ReportsRepository;
import org.cataract.web.presentation.dto.requests.*;
import org.cataract.web.presentation.dto.responses.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportsServiceImpl implements ReportsService {

    private final ReportsRepository reportsRepository;

    private final PatientRepository patientRepository;

    private final ImageService imageService;

    public ReportsServiceImpl(ReportsRepository reportsRepository,
                              PatientRepository patientRepository,
                              ImageService imageService) {
        this.reportsRepository = reportsRepository;
        this.patientRepository = patientRepository;
        this.imageService = imageService;
    }

    @Value("${app.image.filepath}")
    String uploadDir;

    @Value("${app.image.baseurl}")
    String imageUrlPath;

    @PostConstruct
    private void init() {
        try {
            if (uploadDir != null && !Files.exists(Path.of(uploadDir))) {
                Files.createDirectories(Path.of(uploadDir));
            } else {
                Files.createDirectories(Path.of("/app/uploads"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory!", e);
        }
    }


    @Transactional
    public ReportResponseDto saveReportfromApp(Institution institution, ReportRequestDto reportRequestDto) throws Exception {

        String uuid = UUID.randomUUID().toString();
        String shortUuid = uuid.substring(0, 4);
        reportRequestDto.setScanDate(LocalDateTime.now());
        Reports reports = new Reports(reportRequestDto);
        log.debug("[{}] saving eye image {} from app", institution.getName(), reports.getImageIdentifier());
        if (reportRequestDto.getLeftImage() != null) {
            String leftImageFilename = reportRequestDto.getImageIdentifier()
                    + "-L-" + shortUuid + getExtension(reportRequestDto.getLeftImage().getOriginalFilename());
            String leftImageFileUrl = imageService.uploadFile(reportRequestDto.getLeftImage(), leftImageFilename);
            reports.setLImagePath(leftImageFileUrl);
        }
        if (reportRequestDto.getRightImage() != null) {
            String rightImageFilename = reportRequestDto.getImageIdentifier()
                    + "-R-" + shortUuid + getExtension(reportRequestDto.getRightImage().getOriginalFilename());
            String rightImageFileUrl = imageService.uploadFile(reportRequestDto.getRightImage(), rightImageFilename);
            reports.setRImagePath(rightImageFileUrl);
        }
        reports.setPatient(null);
        reports.setInstitution(institution);
        reports = reportsRepository.saveAndFlush(reports);
        log.debug("[{}] saved eye image {} from app", institution.getName(), reports.getImageIdentifier());
        return ReportResponseDto.toDto(reports);
    }

    public String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    @Transactional
    public ReportLinkResponseDto linkReportWithPatient(Institution institution, long reportId, int patientId) {
        Reports reports = reportsRepository.findById(reportId)
                .orElseThrow(ReportNotFoundException::new);

        Patient patient =
                patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                        .orElseThrow(PatientNotFoundException::new);
        reports.setPatient(patient);
        reports.setImageIdentifier(patient.getName() + "=" + patient.getSex());
        reports = reportsRepository.save(reports);
        log.debug("[{}] linked the report {} with patient {}", institution.getName(), reports.getRImagePath(), patient.getName());
        return new ReportLinkResponseDto(patient, reports);
    }

    @Transactional
    public ReportLinkResponseDto unlinkReportWithPatient(Institution institution, Long reportId) {
        Reports reports = reportsRepository.findById(reportId)
                .orElseThrow(ReportNotFoundException::new);

        reports.setPatient(null);
        reports = reportsRepository.save(reports);
        log.debug("[{}] linked the report {}", institution.getName(), reports.getReportId());
        return new ReportLinkResponseDto(reports);

    }

    @Override
    @Transactional
    public ReportCommentResponseDto updateReportComments(Institution institution, Long reportId, ReportCommentRequestDto reportCommentRequestDto) {
        Reports report = reportsRepository.findById(reportId).orElseThrow(ReportNotFoundException::new);
        report.setComments(reportCommentRequestDto.getComments());
        reportsRepository.save(report);
        return ReportCommentResponseDto.toDto(reportId, reportCommentRequestDto.getComments());
    }

    
    @Override
    @Transactional(readOnly = true)
    public ReportCommentResponseDto getReportComments(Institution institution, Long reportId) {
        String reportComment = reportsRepository.findCommentsById(reportId);
        return ReportCommentResponseDto.toDto(reportId, reportComment);
    }

    public Object getReportsByInstitutionAndDateRange(Institution institution, ReportsListRequestDto reportsListRequestDto, Pageable pageable) {

        Specification<Reports> spec = getJpaSpecFromRequest(reportsListRequestDto, institution);

        if (reportsListRequestDto.getSortBy().equals("name"))
            reportsListRequestDto.setSortBy("imageIdentifier");
        Sort.Direction direction = Sort.Direction.fromString(reportsListRequestDto.getSortDir());

        if (pageable.isPaged()) {
            pageable = PageRequest.of(reportsListRequestDto.getPage(), reportsListRequestDto.getSize(),
                    Sort.by(direction, reportsListRequestDto.getSortBy()));
            Page<Reports> reportsList = reportsRepository.findAll(spec, pageable);
            log.debug("[{}] fetched reports list", institution.getName());
            switch (reportsListRequestDto.getLinkStatus()) {
                case 1:
                    reportsList = filterLinkedReports(reportsList);
                    break;
                case 0:
                    reportsList = filterUnlinkedReports(reportsList);
                    break;
                default:
                    break;
            }
            return reportsList.map(ReportSimpleResponseDto::toDto);
        } else {
            Sort sort = Sort.by(direction, reportsListRequestDto.getSortBy());
            List<Reports> reportsList = reportsRepository.findAll(spec, sort);
            log.debug("[{}] fetched reports list", institution.getName());
            switch (reportsListRequestDto.getLinkStatus()) {
                case 1:
                    reportsList = reportsList.stream().filter(r -> r.getPatient() != null).collect(Collectors.toList());
                    break;
                case 0:
                    reportsList = reportsList.stream().filter(r -> r.getPatient() == null).collect(Collectors.toList());
                    break;
                default:
                    break;
            }
            return reportsList.stream().map(ReportSimpleResponseDto::toDto).toList();
        }

    }

    @Transactional(readOnly = true)
    public Object getReportsByPatient(Institution institution, int patientId,
                                      ReportsListRequestDto reportsListRequestDto, Pageable pageable) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        if (reportsListRequestDto.getSortBy().equals("name"))
            reportsListRequestDto.setSortBy("imageIdentifier");

        Sort.Direction direction = Sort.Direction.fromString(reportsListRequestDto.getSortDir());
        if (pageable.isPaged()) {
            pageable = PageRequest.of(reportsListRequestDto.getPage(), reportsListRequestDto.getSize(),
                    Sort.by(direction, reportsListRequestDto.getSortBy()));

            Page<Reports> reportsList = reportsRepository.findByPatientAndInstitution(patient, institution, pageable);
            log.debug("[{}] fetched report list of patient {} {}", institution.getName(), patient.getName(), patient.getPatientId());
            return reportsList.map(ReportDetailResponseDto::toDto);
        } else {
            List<Reports> reportsList = reportsRepository.findByPatientAndInstitution(patient, institution);
            log.debug("[{}] fetched report list of patient {} {}", institution.getName(), patient.getName(), patient.getPatientId());
            return reportsList.stream().map(ReportDetailResponseDto::toDto).toList();

        }
    }

    @Transactional(readOnly = true)
    public List<Reports> getReportListByPatient(Patient patient) {

        List<Reports> reportsList = reportsRepository.findByPatient(patient);
        log.debug("fetched report list of patient {} {}", patient.getName(), patient.getPatientId());
        return reportsList;
    }

    @Transactional(readOnly = true)
    public List<PatientReportResponseDto> getRecentReportByPatientIdAndInstitution(Institution institution, int patientId, int numOfReports) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        Sort.Direction direction = Sort.Direction.fromString("asc");
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by(direction, "reportId"));

        Page<Reports> reportsList = reportsRepository.findByPatientAndInstitution(patient, institution, pageable);
        log.debug("[{}] fetched report list of patientId: {} with num of images: {}", institution.getName(), patientId, numOfReports);
        return reportsList.stream().limit(numOfReports).map(PatientReportResponseDto::toDto).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public ReportDetailResponseDto getReportById(Institution institution, long reportId) {

        Reports reports = getReportEntityById(reportId, institution);
        return ReportDetailResponseDto.toDto(reports);
    }

    @Transactional(readOnly = true)
    public Object getCandidatePatientsByReportId(Institution institution, long reportId,
                                                                   PatientListRequestDto patientListRequestDto, Pageable pageable) {

        Reports reports = getReportEntityById(reportId, institution);
        String patientName = reports.getImageIdentifier().split("=")[0];
        String sexFilter;
        if (patientListRequestDto.getSex() != null)
            sexFilter = patientListRequestDto.getSex();
        else {
            sexFilter = reports.getImageIdentifier().split("=")[1];
        }

        Sort.Direction direction = Sort.Direction.fromString(patientListRequestDto.getSortDir());
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<Reports> reportsListIn1Day = reportsRepository.findByInstitutionAndScanDateAfter(institution, twentyFourHoursAgo);
        List<Patient> patientsWithReportWithinDay = reportsListIn1Day.stream()
                .map(Reports::getPatient).filter(Objects::nonNull).filter(p -> p.getSex().equals(sexFilter)).toList();
        if (pageable.isPaged()) {
            pageable = PageRequest.of(patientListRequestDto.getPage(), patientListRequestDto.getSize(),
                    Sort.by(direction, patientListRequestDto.getSortBy()));

            Page<Patient> potentialPatientList = patientRepository.findAllByNameContainingIgnoreCaseAndInstitution(
                    patientName, institution, pageable);

            Page<Patient> filteredPotentialPatientList = filterPatientPage(potentialPatientList, patientsWithReportWithinDay);

            return filteredPotentialPatientList.map(PatientResponseDto::toDto);
        } else {

            List<Patient> potentialPatients = patientRepository.findAllByNameContainingIgnoreCaseAndInstitution(patientName, institution);
            potentialPatients.removeIf(patientsWithReportWithinDay::contains);
            return potentialPatients;
        }

    }

    public Page<Patient> filterPatientPage(Page<Patient> originalPage, List<Patient> removalList) {
        List<Patient> filteredContent = originalPage.getContent().stream()
                .filter(a -> !removalList.contains(a))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredContent, originalPage.getPageable(), filteredContent.size());
    }

    @Override
    @Transactional
    public void deleteReportById(Institution institution, long reportId) {
        Reports report = getReportEntityById(reportId, institution);
        imageService.deleteFile(report.getLImagePath());
        imageService.deleteFile(report.getRImagePath());
        reportsRepository.delete(report);
    }

    @Override
    @Transactional
    public ReportDetailResponseDto updateReport(Institution institution, long reportId, UpdateReportRequestDto updateReportRequestDto) {
        Reports report = getReportEntityById(reportId, institution);
        if (updateReportRequestDto.getLeftEyeDiagnosis() != null)
            report.setLDiagnosis(updateReportRequestDto.getLeftEyeDiagnosis());
        if (updateReportRequestDto.getRightEyeDiagnosis() != null)
            report.setRDiagnosis(updateReportRequestDto.getRightEyeDiagnosis());
        if (updateReportRequestDto.getLeftEyeRemarks() != null)
            report.setLRemark(updateReportRequestDto.getLeftEyeRemarks());
        if (updateReportRequestDto.getRightEyeRemarks() != null)
            report.setRRemark(updateReportRequestDto.getRightEyeRemarks());
        if (updateReportRequestDto.getComments() != null)
            report.setComments(updateReportRequestDto.getComments());
        reportsRepository.save(report);
        return ReportDetailResponseDto.toDto(report);
    }

    @Transactional(readOnly = true)
    private Reports getReportEntityById(long reportId, Institution institution) {

        Reports reports = reportsRepository.findById(reportId).orElseThrow(ReportNotFoundException::new);
        if (!institution.equals(reports.getInstitution())) {
            throw new ReportNotFoundException();
        }

        return reports;
    }

    @Transactional(readOnly = true)
    public long getUnlinkedReportCount(Institution institution) {
        return reportsRepository.countUnlinkedReports(institution.getInstitutionId());
    }

    public Page<Reports> filterLinkedReports(Page<Reports> reportsPage) {
        List<Reports> linkedReports = reportsPage.getContent().stream()
                .filter(report -> report.getPatient() != null)
                .collect(Collectors.toList());

        return new PageImpl<>(linkedReports, reportsPage.getPageable(), linkedReports.size());
    }

    public Page<Reports> filterUnlinkedReports(Page<Reports> reportsPage) {
        List<Reports> unlinkedReports = reportsPage.getContent().stream()
                .filter(report -> report.getPatient() == null)
                .collect(Collectors.toList());

        return new PageImpl<>(unlinkedReports, reportsPage.getPageable(), unlinkedReports.size());
    }


    private Specification<Reports> getJpaSpecFromRequest(ReportsListRequestDto reportsListRequestDto, Institution institution) {

        Specification<Reports> spec = Specification.where(null);

        if (Objects.nonNull(institution)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("institution"), institution)
            );
        }

        if (Objects.nonNull(reportsListRequestDto.getQuery()) && !reportsListRequestDto.getQuery().isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("imageIdentifier")), "%" + reportsListRequestDto.getQuery().toLowerCase() + "%")
            );
        }

        if (Objects.nonNull(reportsListRequestDto.getSex()) && !reportsListRequestDto.getSex().isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.substring(
                            root.get("imageIdentifier"),
                            criteriaBuilder.sum(criteriaBuilder.locate(root.get("imageIdentifier"), "="),
                                    criteriaBuilder.literal(1))
                    ), reportsListRequestDto.getSex().toLowerCase())
            );
        }
        if (Objects.nonNull(reportsListRequestDto.getStatus())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("lAiResult"), reportsListRequestDto.getStatus().getLabel()),
                            criteriaBuilder.equal(root.get("rAiResult"), reportsListRequestDto.getStatus().getLabel())
                    )
            );
        }

        if (Objects.nonNull(reportsListRequestDto.getStartDate())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("scanDate"), reportsListRequestDto.getStartDate())
            );
        }

        if (Objects.nonNull(reportsListRequestDto.getEndDate())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("scanDate"), reportsListRequestDto.getEndDate())
            );
        }
        return spec;
    }
}

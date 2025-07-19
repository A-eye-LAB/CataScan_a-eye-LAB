package org.cataract.web.application.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.FindReportCandidateService;
import org.cataract.web.application.service.ImageService;
import org.cataract.web.application.service.ReportsService;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.Patient;
import org.cataract.web.domain.Report;
import org.cataract.web.domain.exception.PatientNotFoundException;
import org.cataract.web.domain.exception.ReportNotFoundException;
import org.cataract.web.infra.PatientRepository;
import org.cataract.web.infra.ReportsRepository;
import org.cataract.web.presentation.dto.responses.ReportCommentResponseDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportsServiceImpl implements ReportsService {

    private final ReportsRepository reportsRepository;

    private final PatientRepository patientRepository;

    private final ImageService imageService;

    private final FindReportCandidateService findReportCandidateService;

    public ReportsServiceImpl(ReportsRepository reportsRepository,
                              PatientRepository patientRepository,
                              ImageService imageService,
                              FindReportCandidateService findReportCandidateService) {
        this.reportsRepository = reportsRepository;
        this.patientRepository = patientRepository;
        this.imageService = imageService;
        this.findReportCandidateService = findReportCandidateService;
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
        Report report = new Report(reportRequestDto);
        log.debug("[{}] saving eye image {} from app", institution.getName(), report.getImageIdentifier());
        if (reportRequestDto.getLeftImage() != null) {
            String leftImageFilename = reportRequestDto.getImageIdentifier()
                    + "-L-" + shortUuid + getExtension(reportRequestDto.getLeftImage().getOriginalFilename());
            String leftImageFileUrl = imageService.uploadFile(reportRequestDto.getLeftImage(), leftImageFilename, institution.getImageStorage());
            report.setLImagePath(leftImageFileUrl);
        }
        if (reportRequestDto.getRightImage() != null) {
            String rightImageFilename = reportRequestDto.getImageIdentifier()
                    + "-R-" + shortUuid + getExtension(reportRequestDto.getRightImage().getOriginalFilename());
            String rightImageFileUrl = imageService.uploadFile(reportRequestDto.getRightImage(), rightImageFilename, institution.getImageStorage());
            report.setRImagePath(rightImageFileUrl);
        }
        report.setPatient(null);
        report.setInstitution(institution);
        report.setComments(reportRequestDto.getComments());
        report = reportsRepository.save(report);
        log.debug("[{}] saved eye image {} from app", institution.getName(), report.getImageIdentifier());
        return ReportResponseDto.toDto(report);
    }

    public String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    @Transactional
    public ReportLinkResponseDto linkReportWithPatient(Institution institution, long reportId, int patientId) {
        Report report = reportsRepository.findById(reportId)
                .orElseThrow(ReportNotFoundException::new);

        Patient patient =
                patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                        .orElseThrow(PatientNotFoundException::new);
        report.setPatient(patient);
        report.setImageIdentifier(patient.getName() + "=" + patient.getSex());
        report = reportsRepository.save(report);
        log.debug("[{}] linked the report {} with patient {}", institution.getName(), report.getRImagePath(), patient.getName());
        return new ReportLinkResponseDto(patient, report);
    }

    @Transactional
    public ReportLinkResponseDto unlinkReportWithPatient(Institution institution, Long reportId) {
        Report report = reportsRepository.findById(reportId)
                .orElseThrow(ReportNotFoundException::new);

        report.setPatient(null);
        report = reportsRepository.save(report);
        log.debug("[{}] linked the report {}", institution.getName(), report.getReportId());
        return new ReportLinkResponseDto(report);

    }

    @Override
    @Transactional
    public ReportCommentResponseDto updateReportComments(Institution institution, Long reportId, ReportCommentRequestDto reportCommentRequestDto) {
        Report report = reportsRepository.findById(reportId).orElseThrow(ReportNotFoundException::new);
        report.setComments(reportCommentRequestDto.getComments());
        reportsRepository.save(report);
        return ReportCommentResponseDto.toDto(reportId, reportCommentRequestDto.getComments());
    }

    @Override
    @Transactional
    public ReportCommentResponseDto getReportComments(Institution institution, Long reportId) {
        String reportComment = reportsRepository.findCommentsById(reportId);
        return ReportCommentResponseDto.toDto(reportId, reportComment);
    }

    @Transactional
    public Object getReportsByInstitutionAndDateRange(List<Institution> institutionList, ReportsListRequestDto reportsListRequestDto, Pageable pageable) {

        Specification<Report> spec = getJpaSpecFromRequest(reportsListRequestDto, institutionList);

        if (reportsListRequestDto.getSortBy().equals("name"))
            reportsListRequestDto.setSortBy("imageIdentifier");
        Sort.Direction direction = Sort.Direction.fromString(reportsListRequestDto.getSortDir());

        if (pageable.isPaged()) {
            pageable = PageRequest.of(reportsListRequestDto.getPage(), reportsListRequestDto.getSize(),
                    Sort.by(direction, reportsListRequestDto.getSortBy()));
            Page<Report> reportsList = reportsRepository.findAll(spec, pageable);
            log.debug("[{}] fetched reports list", institutionList);
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
            List<Report> reportList = reportsRepository.findAll(spec, sort);
            log.debug("[{}] fetched reports list", institutionList);
            switch (reportsListRequestDto.getLinkStatus()) {
                case 1:
                    reportList = reportList.stream().filter(r -> r.getPatient() != null).collect(Collectors.toList());
                    break;
                case 0:
                    reportList = reportList.stream().filter(r -> r.getPatient() == null).collect(Collectors.toList());
                    break;
                default:
                    break;
            }
            return reportList.stream().map(ReportSimpleResponseDto::toDto).toList();
        }

    }


    @Transactional
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

            Page<Report> reportsList = reportsRepository.findByPatientAndInstitution(patient, institution, pageable);
            log.debug("[{}] fetched report list of patient {} {}", institution.getName(), patient.getName(), patient.getPatientId());
            return reportsList.map(ReportDetailResponseDto::toDto);
        } else {
            Sort sort = Sort.by( Sort.Direction.fromString(reportsListRequestDto.getSortDir()), reportsListRequestDto.getSortBy());
            List<Report> reportList = reportsRepository.findByPatientAndInstitution(patient, institution, sort);
            log.debug("[{}] fetched report list of patient {} {}", institution.getName(), patient.getName(), patient.getPatientId());
            return reportList.stream().map(ReportDetailResponseDto::toDto).toList();

        }
    }

    @Transactional(readOnly = true)
    public List<PatientReportResponseDto> getRecentReportByPatientIdAndInstitution(Institution institution, int patientId, int numOfReports) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        Sort.Direction direction = Sort.Direction.fromString("asc");
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by(direction, "reportId"));

        Page<Report> reportsList = reportsRepository.findByPatientAndInstitution(patient, institution, pageable);
        log.debug("[{}] fetched report list of patientId: {} with num of images: {}", institution.getName(), patientId, numOfReports);
        return reportsList.stream().limit(numOfReports).map(PatientReportResponseDto::toDto).collect(Collectors.toList());

    }

    @Transactional
    public ReportDetailResponseDto getReportById(long reportId) {

        Report report = getReportEntityById(reportId);
        return ReportDetailResponseDto.toDto(report);
    }

    @Transactional(readOnly = true)
    public Object getCandidatePatientsByReportId(Institution institution, long reportId,
                                                                   PatientListRequestDto patientListRequestDto, Pageable pageable) {

        Report report = getReportEntityById(reportId);
        String patientName = report.getImageIdentifier().split("=")[0];
        String sexFilter;
        if (patientListRequestDto.getSex() != null)
            sexFilter = patientListRequestDto.getSex();
        else {
            sexFilter = report.getImageIdentifier().split("=")[1];
        }
        Sort.Direction direction = Sort.Direction.fromString(patientListRequestDto.getSortDir());
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<Report> reportListIn1Day = reportsRepository.findByInstitutionAndScanDateAfter(institution, twentyFourHoursAgo);
        List<Patient> patientsWithReportWithinDay = new ArrayList<>();
        if (!patientListRequestDto.isQuery()) {
            patientsWithReportWithinDay = reportListIn1Day.stream()
                    .map(Report::getPatient).filter(Objects::nonNull).filter(p -> p.getSex().equals(sexFilter)).toList();
        }
        if (pageable.isPaged()) {
            pageable = PageRequest.of(patientListRequestDto.getPage(), patientListRequestDto.getSize(),
                    Sort.by(direction, patientListRequestDto.getSortBy()));
            Page<Patient> potentialPatientList = patientRepository.findAllByNameContainingIgnoreCaseAndInstitution(
                    patientName, institution, pageable);
            Page<Patient> filteredPotentialPatientList = filterPatientPage(potentialPatientList, patientsWithReportWithinDay, patientName);
            return filteredPotentialPatientList.map(PatientResponseDto::toDto);
        } else {
            List<Patient> potentialPatients = patientRepository.findAllByInstitution(institution);
            potentialPatients.removeIf(patientsWithReportWithinDay::contains);
            List<PatientResponseDto> results = potentialPatients.stream()
                    .map(patient -> new PatientSimilarityCalculation(
                            patient,
                            findReportCandidateService.calculateDistance(patient.getName(), patientName)
                    ))
                    .sorted(Comparator.comparingInt(PatientSimilarityCalculation::distance))
                    .limit(20)
                    .map(patientCalculation ->
                            PatientResponseDto.toDto(patientCalculation.patient()))
                    .toList();
            return results;
        }
    }

    public Page<Patient> filterPatientPage(Page<Patient> originalPage, List<Patient> removalList, String patientName) {
        List<Patient> filteredContent = originalPage.getContent().stream()
                .filter(a -> !removalList.contains(a))
                .map(patient -> new PatientSimilarityCalculation(
                        patient,
                        findReportCandidateService.calculateDistance(patient.getName(), patientName)
                ))
                .sorted(Comparator.comparingInt(PatientSimilarityCalculation::distance))
                .limit(10)
                .map(PatientSimilarityCalculation::patient).collect(Collectors.toList());

        return new PageImpl<>(filteredContent, originalPage.getPageable(), filteredContent.size());
    }

    @Override
    @Transactional
    public void deleteReportById(long reportId) {
        Report report = getReportEntityById(reportId);
        imageService.deleteFile(report.getLImagePath(), report.getInstitution().getImageStorage());
        imageService.deleteFile(report.getRImagePath(), report.getInstitution().getImageStorage());
        reportsRepository.delete(report);
    }

    @Override
    @Transactional
    public ReportDetailResponseDto updateReport(Institution institution, long reportId, UpdateReportRequestDto updateReportRequestDto) {
        Report report = getReportEntityById(reportId);
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

    private Report getReportEntityById(long reportId) {

        Report report = reportsRepository.findById(reportId).orElseThrow(ReportNotFoundException::new);
        return report;
    }

    @Transactional(readOnly = true)
    public long getUnlinkedReportCount(Institution institution) {
        return reportsRepository.countUnlinkedReports(institution.getInstitutionId());
    }

    public Page<Report> filterLinkedReports(Page<Report> reportsPage) {
        List<Report> linkedReports = reportsPage.getContent().stream()
                .filter(report -> report.getPatient() != null)
                .collect(Collectors.toList());

        return new PageImpl<>(linkedReports, reportsPage.getPageable(), linkedReports.size());
    }

    public Page<Report> filterUnlinkedReports(Page<Report> reportsPage) {
        List<Report> unlinkedReports = reportsPage.getContent().stream()
                .filter(report -> report.getPatient() == null)
                .collect(Collectors.toList());

        return new PageImpl<>(unlinkedReports, reportsPage.getPageable(), unlinkedReports.size());
    }


    private Specification<Report> getJpaSpecFromRequest(ReportsListRequestDto reportsListRequestDto, List<Institution> institutionList) {

        Specification<Report> spec = Specification.where(null);

        if (institutionList != null && !institutionList.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.get("institution").in(institutionList)
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
                    criteriaBuilder.lessThanOrEqualTo(root.get("scanDate"), reportsListRequestDto.getEndDate().atTime(23, 59, 59))
            );
        }
        return spec;
    }
}

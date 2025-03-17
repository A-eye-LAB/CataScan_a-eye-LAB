package org.cataract.web.presentation.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.ReportsService;
import org.cataract.web.application.service.UserService;
import org.cataract.web.domain.AiResult;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.exception.BadUploadRequestException;
import org.cataract.web.domain.exception.InvalidTokenException;
import org.cataract.web.domain.exception.PatientNotFoundException;
import org.cataract.web.domain.exception.ReportNotFoundException;
import org.cataract.web.presentation.dto.responses.ReportCommentResponseDto;
import org.cataract.web.presentation.dto.ResponseDto;
import org.cataract.web.presentation.dto.requests.*;
import org.cataract.web.presentation.dto.responses.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReportsController {

    private final ReportsService reportsService;

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> uploadReport(@RequestParam(value = "leftImage", required = false) MultipartFile leftImage,
                                                          @RequestParam(value = "rightImage", required = false) MultipartFile rightImage,
                                                          @RequestParam("imageId") String imageIdentifier,
                                                          @RequestParam(value = "leftAiResult", required = false) String leftAiResultStr,
                                                          @RequestParam(value = "rightAiResult", required = false) String rightAiResultStr,
                                                          @RequestParam(value = "comments", required = false) String comments,
                                                          Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] imageIdentifier {} Received request to upload report", institution.getName(), imageIdentifier);
        if ((leftImage == null || leftImage.isEmpty()) && (rightImage == null || rightImage.isEmpty())) {
            throw new BadUploadRequestException();
        }
        AiResult leftAiResult = AiResult.UNGRADABLE;
        AiResult rightAiResult = AiResult.UNGRADABLE;
        if (leftAiResultStr != null)
            leftAiResult = AiResult.fromLabel(leftAiResultStr);
        if (rightAiResultStr != null)
            rightAiResult = AiResult.fromLabel(rightAiResultStr);
        ReportRequestDto reportRequestDto = new ReportRequestDto(leftImage, rightImage, leftAiResult, rightAiResult,
                imageIdentifier, comments);

        try {
            ReportResponseDto reportResponseDto = reportsService.saveReportfromApp(institution, reportRequestDto);
            log.info("[{}] imageIdentifier: {} successfully uploaded", institution.getName(), imageIdentifier);
            return ResponseEntity.ok(reportResponseDto);
        } catch (InvalidTokenException ex) {
            log.error("[{}] imageIdentifier: {} failed to upload report due to invalid token", institution.getName(), imageIdentifier, ex);
            return ResponseEntity.badRequest().body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("[{}] imageIdentifier: {} failed to upload report", institution.getName(), imageIdentifier, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @PostMapping("/{reportId}/link-patient/{patientId}")
    public ResponseEntity<ResponseDto> linkReportWithPatient(@PathVariable Long reportId,
                                                                       @PathVariable int patientId,
                                                                       Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] reportId: {} Received request to link with patientId: {}", institution.getName(), reportId, patientId);
        try {
            ReportLinkResponseDto reportLinkResponseDto =
                    reportsService.linkReportWithPatient(institution, reportId, patientId);
            log.info("[{}] reportId: {} successfully linked with patientId: {}", institution.getName(), reportId, patientId);
            return ResponseEntity.accepted().body(reportLinkResponseDto);
        } catch (ReportNotFoundException ex) {
            log.error("[{}] reportId: {} failed to link with patientId {} as report does not exist",
                    institution.getName(), reportId, patientId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (PatientNotFoundException ex) {
            log.error("[{}] reportId: {} failed to link with patientId {} as patient does not exist",
                    institution.getName(), reportId, patientId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("[{}] reportId: {} failed to link with patientId: {}", institution.getName(), reportId, patientId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @PostMapping("/{reportId}/unlink-patient")
    public ResponseEntity<ResponseDto> unlinkReportWithPatient(@PathVariable Long reportId,
                                                                       Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] reportId: {} Received request to unlink patient", institution.getName(), reportId);
        try {
            ReportLinkResponseDto reportLinkResponseDto =
                    reportsService.unlinkReportWithPatient(institution, reportId);
            log.info("[{}] reportId: {} successfully unlinked", institution.getName(), reportId);
            return ResponseEntity.ok(reportLinkResponseDto);
        } catch (Exception ex) {
            log.warn("[{}] reportId: {} failed to unlink", institution.getName(), reportId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @GetMapping
    public ResponseEntity<?> getReportListFromDateRange(
            @RequestParam(value = "q", required = false) String query,
            @JsonFormat(pattern = "yyyy-MM-dd")
            @RequestParam(required = false) String startDate,
            @JsonFormat(pattern = "yyyy-MM-dd")
            @RequestParam(required = false) String endDate,
            @Pattern(
                    regexp = "^(name|scanDate|updatedAt)$",
                    message = "only accepts 'name','scanDate' or 'updatedAt' as 'sortBy'"
            )
            @RequestParam(required = false) String sortBy,
            @Pattern(
                    regexp = "^(asc|desc)$",
                    message = "only accepts 'asc' or 'desc' as 'sortDir'"
            )
            @RequestParam(required = false) String sortDir,
            @Pattern(
                    regexp = "^(male|female)$",
                    message = "only accepts 'male', 'female' or 'other' as 'sex'"
            )
            @RequestParam(required = false) String sex,
            @Pattern(
                    regexp = "^(lowRisk|requiresAttention|ungradable)$",
                    message = "only accepts 'lowRisk','requiresAttention' or 'ungradable' as 'status'"
            )
            @RequestParam(value="status", required = false) String aiResultStr,
            @Range(max=1)
            @RequestParam(required = false) Integer linkStatus,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        AiResult status = null;
        if (aiResultStr != null)
            status = AiResult.fromLabel(aiResultStr);
        ReportsListRequestDto reportsListRequestDto =
                new ReportsListRequestDto(query, startDate, endDate, sortBy, sortDir, sex, status, linkStatus);
        Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
        log.info("[{}] Received request to retrieve report list by {}", institution.getName(), reportsListRequestDto);
        try {
            Object reportSimpleResponseDtoList =
                    reportsService.getReportsByInstitutionAndDateRange(institution, reportsListRequestDto, pageable);
            log.info("[{}] reports list retrieved successfully", institution.getName());
            return ResponseEntity.ok(reportSimpleResponseDtoList);
        } catch (Exception ex) {
            log.error("[{}] reports list retrieved failed", institution.getName(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getReportByPatient(
            @PathVariable int patientId,
            @Size(max=30, message = "query must be smaller than 30 characters")
            @RequestParam(value = "q", required = false) String query,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) String startDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Pattern(
                    regexp = "^(name|scanDate|updatedAt)$",
                    message = "only accepts 'name','scanDate' or 'updatedAt' as 'sortBy'"
            )
            @RequestParam(required = false) String sortBy,
            @Pattern(
                    regexp = "^(asc|desc)$",
                    message = "only accepts 'asc' or 'desc' as 'sortDir'"
            )
            @RequestParam(required = false) String sortDir,
            @Pattern(
                    regexp = "^(male|female|other)$",
                    message = "only accepts 'male', 'female' or 'other' as 'sex'"
            )
            @RequestParam(required = false) String sex,
            @Pattern(
                    regexp = "^(lowRisk|requiresAttention|ungradable)$",
                    message = "only accepts 'lowRisk','requiresAttention' or 'ungradable' as 'status'"
            )
            @RequestParam(value="status", required = false) String aiResultStr,
            @Range(max=1)
            @RequestParam(required = false) Integer linkStatus,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        AiResult status = null;
        if (aiResultStr != null)
            status = AiResult.fromLabel(aiResultStr);
        ReportsListRequestDto reportsListRequestDto =
                new ReportsListRequestDto(query, startDate, endDate, page, size,sortBy, sortDir, sex, status, linkStatus);
        log.info("[{}] patientId: {} Received request to retrieve report", institution.getName(), patientId);
        try {
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
            Object reportDetailResponseDtos = reportsService.getReportsByPatient(institution, patientId, reportsListRequestDto, pageable);
            log.info("[{}] patientId: {} report list retrieved successfully", institution.getName(), patientId);
            if (pageable.isPaged()) {
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<ReportDetailResponseDto>)reportDetailResponseDtos));
            }
            return ResponseEntity.ok(reportDetailResponseDtos);

        } catch (Exception ex) {
            log.warn("[{}] patientId: {} report list retrieved failed", institution.getName(), patientId);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ResponseDto> getReportsById(
            @PathVariable long reportId,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] reportId: {} Received request to retrieve detailed info", institution.getName(), reportId);
        try {
            ReportDetailResponseDto reportDetailResponseDto = reportsService.getReportById(institution, reportId);
            log.info("[{}] reportId: {} report info retrieved successfully", institution.getName(), reportId);
            return ResponseEntity.ok(reportDetailResponseDto);
        } catch (Exception ex) {
            log.warn("[{}] reportId: {} report info retrieved failed", institution.getName(), reportId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<ResponseDto> updateReportById(
            @PathVariable long reportId,
            @Valid @RequestBody UpdateReportRequestDto updateReportRequestDto,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] reportId: {} Received request to update", institution.getName(), reportId);
        try {
            ReportDetailResponseDto reportDetailResponseDto = reportsService.updateReport(institution, reportId, updateReportRequestDto);
            log.info("[{}] reportId: {} report updated successfully", institution.getName(), reportId);
            return ResponseEntity.ok(reportDetailResponseDto);
        } catch (Exception ex) {
            log.warn("[{}] reportId: {} report update failed", institution.getName(), reportId);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<ErrorResponseDto> deleteReportById(
            @PathVariable long reportId,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] reportId: {} Received request to delete", institution.getName(), reportId);
        try {
            reportsService.deleteReportById(institution, reportId);
            log.info("[{}] reportId: {} report deleted successfully", institution.getName(), reportId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto("successfully deleted or report does not exist"));
        } catch (Exception ex) {
            log.warn("[{}] reportId: {} report delete failed", institution.getName(), reportId);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @GetMapping("/{reportId}/candidates-list")
    public ResponseEntity<?> getReportCandidatePatients(
            @PathVariable long reportId,
            @Size(max=30, message = "query must be smaller than 30 characters")
            @RequestParam(value = "q", required = false) String query,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate startDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer page,
            @Range(min = 1, max = 1000, message = "page size needs to be between 1 and 1000")
            @RequestParam(required = false) Integer size,
            @Pattern(
                    regexp = "^(name|dateOfBirth|sex)$",
                    message = "only accepts 'name', 'dateOfBirth', 'sex' as 'sortBy'"
            )
            @RequestParam(required = false) String sortBy,
            @Pattern(
                    regexp = "^(asc|desc)$",
                    message = "only accepts 'asc' or 'desc' as 'sortDir'"
            )
            @RequestParam(required = false) String sortDir,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] reportId: {} Received request to retrieve candidate list", institution.getName(), reportId);
        try {
            PatientListRequestDto patientListRequestDto =
                    new PatientListRequestDto(query, startDate, endDate, page, size,sortBy, sortDir);
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();

            Object patientResponseDtos = reportsService.getCandidatePatientsByReportId(
                            institution, reportId, patientListRequestDto, pageable);
            if (pageable.isPaged()) {
                log.info("[{}] reportId: {} candidate patients retrieved successfully", institution.getName(), reportId);
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<PatientResponseDto>)patientResponseDtos));
            }
            return ResponseEntity.ok(patientResponseDtos);
        } catch (Exception ex) {
            log.warn("[{}] reportId: {} candidate patients retrieved failed", institution.getName(), reportId);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @GetMapping("/unlinked-num")
    public ResponseEntity<Long> getUnlinkedReportsNum(
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] Received request to retrieve number of unlinked reports", institution.getName());
        try {
            long unlinkedReportsCount = reportsService.getUnlinkedReportCount(institution);
            log.info("[{}] unlinked reports count retrieved successfully", institution.getName());
            return ResponseEntity.ok(unlinkedReportsCount);
        } catch (Exception ex) {
            log.warn("[{}] unlinked reports count retrieved failed", institution.getName());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{reportId}/comments")
    public ResponseEntity<ResponseDto> getReportComments(@PathVariable Long reportId,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] reportId: {} Received request to retrieve comment", institution.getName(), reportId);
        try {
            ReportCommentResponseDto reportComments = reportsService.getReportComments(institution, reportId);
            log.info("[{}] reportId: {} comment retrieved successfully", institution.getName(), reportId);
            return ResponseEntity.ok(reportComments);
        } catch (Exception ex) {
            log.warn("[{}] reportId: {} comment retrieved failed", institution.getName(), reportId);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @PutMapping("/{reportId}/comments")
    public ResponseEntity<ResponseDto> putReportComments(@PathVariable Long reportId,
                                                         @RequestBody ReportCommentRequestDto reportCommentRequestDto,
                                                         Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] reportId: {} Received request to update comment", institution.getName(), reportId);
        try {
            ReportCommentResponseDto reportComments = reportsService.updateReportComments(institution, reportId, reportCommentRequestDto);
            log.info("[{}] reportId: {} comment update success", institution.getName(), reportId);
            return ResponseEntity.ok(reportComments);
        } catch(ReportNotFoundException ex) {
            log.error("[{}] reportId: {} report comment update failed because it does not exist", institution.getName(), reportId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("[{}] reportId: {} report comment update failed", institution.getName(), reportId);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

}
package org.cataract.web.presentation.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.InstitutionService;
import org.cataract.web.application.service.PatientService;
import org.cataract.web.application.service.ReportsService;
import org.cataract.web.domain.AiResult;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.exception.ReportNotFoundException;
import org.cataract.web.presentation.dto.ResponseDto;
import org.cataract.web.presentation.dto.requests.PatientListRequestDto;
import org.cataract.web.presentation.dto.requests.ReportsListRequestDto;
import org.cataract.web.presentation.dto.responses.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminInstitutionController {

    private final InstitutionService institutionService;

    private final PatientService patientService;

    private final ReportsService reportsService;

    @GetMapping("/institution-list")
    public ResponseEntity<?> getInstitutionPages(
            @RequestParam(required = false) Integer page,
            @Range(min = 1, max = 1000, message = "page size needs to be between 1 and 1000")
            @RequestParam(required = false) Integer size) {
        log.info("ADMIN Received request to retrieve institution list");
        try {
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
            Object institutionResponseDtoList = institutionService.getAllInstitutions(pageable);
            log.info("ADMIN retrieved institution list successfully");
            if (pageable.isPaged()) {
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<InstitutionResponseDto>) institutionResponseDtoList));
            }
            return ResponseEntity.ok(institutionResponseDtoList);
        } catch(ClassCastException ex) {
            log.error("ADMIN institution list retrival failed to cast page or list", ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN institution list retrieval failed", ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @GetMapping("/patient-list")
    public ResponseEntity<?> getPatientList(
            @Size(max = 30, message = "query must be smaller than 30 characters")
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "institution", required = false) String institutionName,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate startDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer page,
            @Range(min = 1, max = 1000, message = "page size needs to be between 1 and 1000")
            @RequestParam(required = false) Integer size,
            @Pattern(
                    regexp = "^(name|dateOfBirth|createdAt|updatedAt)$",
                    message = "only accepts 'name', 'dateOfBirth', 'createdAt', or 'updatedAt' as 'sortBy'"
            )
            @RequestParam(required = false) String sortBy,
            @Pattern(
                    regexp = "^(asc|desc)$",
                    message = "only accepts 'asc' or 'desc' as 'sortDir'"
            )
            @RequestParam(required = false) String sortDir,
            @Range(min = 0, max = 1, message = "dataStatus is either 0 or 1")
            @RequestParam(required = false) Integer dataStatus,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirthFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirthTo,
            Authentication authentication) {
        String username = authentication.getName();
        PatientListRequestDto patientListRequestDto =
                new PatientListRequestDto(query, startDate, endDate, page, size, sortBy, sortDir,
                        dataStatus, sex, dateOfBirthFrom, dateOfBirthTo);
        try {
            Object patientResponseDtoList;
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
            log.info("ADMIN [{}] Received request to get patient list {} with query {}", institutionName, patientListRequestDto, query);
            if (institutionName != null) {
                Institution institution = institutionService.getInstitutionByName(institutionName);
                patientResponseDtoList = patientService.getPatientsByInstitution(institution, patientListRequestDto, pageable);
            } else {
                institutionName = "ALL";
                patientResponseDtoList = patientService.getPatientsByInstitution(null, patientListRequestDto, pageable);
            }
            log.info("ADMIN [{}] patient list {} retrieval success", institutionName, patientListRequestDto);
            if (pageable.isUnpaged()) {
                return ResponseEntity.ok(patientResponseDtoList);
            }
            return ResponseEntity.ok(new OffsetPaginationResult<>((Page<PatientResponseDto>) patientResponseDtoList));
        } catch (Exception ex) {
            log.error("ADMIN [{}] patient list {} retrieval failed", institutionName, patientListRequestDto, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @GetMapping("/institution/{institutionId}/patients/{patientId}")
    public ResponseEntity<ResponseDto> getPatient(@PathVariable Integer institutionId,
                                                         @PathVariable Integer patientId) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        try {
            FullPatientDataDto patientDataDto = patientService.getFullPatientData(institution, patientId, 3);
            log.info("ADMIN [{}] patientId: {} retrieved", institution.getName(), patientId);
            return ResponseEntity.ok(patientDataDto);
        } catch (Exception ex) {
            log.error("ADMIN [{}] patientId: {} failed to retrieve", institution.getName(), patientId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @DeleteMapping("/institution/{institutionId}/patients/{patientId}")
    public ResponseEntity<ErrorResponseDto> deletePatient(@PathVariable Integer institutionId,
                                                          @PathVariable Integer patientId) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        try {
            patientService.deletePatientAndDataPermanently(institution, patientId);
            log.info("ADMIN [{}] patientId: {} deleted", institution.getName(), patientId);
            return ResponseEntity.ok(new ErrorResponseDto("deleted or not found"));
        } catch (Exception ex) {
            log.error("ADMIN [{}] patientId: {} delete failed", institution.getName(), patientId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @PutMapping("/institution/{institutionId}/patients/{patientId}")
    public ResponseEntity<ErrorResponseDto> restoreDeletedPatient(@PathVariable Integer institutionId,
                                                           @PathVariable Integer patientId) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        try {
            patientService.recoverPatient(institution, patientId);
            log.info("ADMIN [{}] patient Id {} restored", institution.getName(), patientId);
            return ResponseEntity.ok(new ErrorResponseDto("successfully recovered deleted patient"));
        } catch (Exception ex) {
            log.error("ADMIN [{}] patient Id {} restored failed", institution.getName(), patientId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @GetMapping("/report-list")
    public ResponseEntity<?> getReportListFromDateRange(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "institution", required = false) String institutionName,
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
                    regexp = "^(male|female)$",
                    message = "only accepts 'male', 'female' or 'other' as 'sex'"
            )
            @RequestParam(required = false) String sex,
            @Pattern(
                    regexp = "^(lowRisk|requiresAttention|ungradable)$",
                    message = "only accepts 'lowRisk','requiresAttention' or 'ungradable' as 'status'"
            )
            @RequestParam(value = "status", required = false) String aiResultStr,
            @Range(max=1)
            @RequestParam(required = false) Integer linkStatus,
            Authentication authentication) {
        String username = authentication.getName();
        AiResult status = null;
        if (aiResultStr != null) {
            status = AiResult.fromLabel(aiResultStr);
        }
        ReportsListRequestDto reportsListRequestDto =
                new ReportsListRequestDto(query, startDate, endDate, page, size, sortBy, sortDir, sex, status, linkStatus);
        log.info("ADMIN [{}] Received request to retrieve report list by {}", institutionName, reportsListRequestDto);
        Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
        try {
            Institution institution = null;
            if (institutionName != null) {
                institution = institutionService.getInstitutionByName(institutionName);
            } else {
                institutionName = "ALL";
            }
            Object reportSimpleResponseDtos = reportsService.getReportsByInstitutionAndDateRange(institution, reportsListRequestDto, pageable);
            log.info("ADMIN [{}] reports list retrieved successfully", institutionName);
            if (pageable.isPaged()) {
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<ReportSimpleResponseDto>)reportSimpleResponseDtos));
            }
            return ResponseEntity.ok(reportSimpleResponseDtos);

        } catch (Exception ex) {
            log.error("ADMIN [{}] reports list retrieved failed", institutionName, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @GetMapping("/institution/{institutionId}/reports/{reportId}")
    public ResponseEntity<ResponseDto> getDetailReport(@PathVariable Integer institutionId,
                                                                   @PathVariable Long reportId) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        try {
            ReportDetailResponseDto reportDetailResponseDto = reportsService.getReportById(institution, reportId);
            log.info("ADMIN [{}] reportId: {} retrieved by ADMIN", institution.getName(), reportId);
            return ResponseEntity.ok(reportDetailResponseDto);
        } catch (ReportNotFoundException ex) {
            log.warn("ADMIN [{}] reportId: {} not found ADMIN", institution.getName(), reportId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN [{}] reportId: {} failed to retrieve ADMIN", institution.getName(), reportId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @DeleteMapping("/institution/{institutionId}/reports/{reportId}")
    public ResponseEntity<ErrorResponseDto> deleteReport(@PathVariable Integer institutionId,
                                                         @PathVariable Long reportId) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        try {
            reportsService.deleteReportById(institution, reportId);
            log.info("ADMIN [{}] reportId: {} deleted", institution.getName(), reportId);
            return ResponseEntity.ok(new ErrorResponseDto("deleted or not found"));
        } catch (ReportNotFoundException ex) {
            log.warn("ADMIN [{}] reportId: {} not found", institution.getName(), reportId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN [{}] reportId: {} delete failed by internal error", institution.getName(), reportId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

}

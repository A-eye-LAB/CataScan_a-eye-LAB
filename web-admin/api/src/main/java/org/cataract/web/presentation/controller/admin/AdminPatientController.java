package org.cataract.web.presentation.controller.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.InstitutionService;
import org.cataract.web.application.service.PatientProfileService;
import org.cataract.web.application.service.PatientService;
import org.cataract.web.application.service.ReportsService;
import org.cataract.web.domain.AiResult;
import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.ResponseDto;
import org.cataract.web.presentation.dto.requests.PatientListRequestDto;
import org.cataract.web.presentation.dto.requests.ReportsListRequestDto;
import org.cataract.web.presentation.dto.responses.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminPatientController {

    private final InstitutionService institutionService;

    private final PatientService patientService;
    private final PatientProfileService patientProfileService;
    private final ReportsService reportsService;

    @GetMapping("/patients")
    public ResponseEntity<?> getPatientList(
            @Size(max = 30, message = "query must be smaller than 30 characters")
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "institution", required = false) List<String> institutions,
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
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();

            List<Institution> institutionList = new ArrayList<>();
            if (institutions != null) {
                for (String institutionName : institutions) {
                    institutionList.add(institutionService.getInstitutionByName(institutionName));
                    log.info("ADMIN [{}] Received request to get patient list {} with query {}",
                            institutionName, patientListRequestDto, query);
                }

            } else {
                log.info("ADMIN [ALL] Received request to get patient list {} with query {}",
                        patientListRequestDto, query);
            }
            var patientResponseDtoList = patientService.getPatientsByInstitution(institutionList, patientListRequestDto, pageable);
            log.info("ADMIN [{}] patient list {} retrieval success", institutions, patientListRequestDto);
            if (pageable.isPaged()) {
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<PatientResponseDto>) patientResponseDtoList));
            }
            return ResponseEntity.ok(patientResponseDtoList);
        } catch (Exception ex) {
            log.error("ADMIN [{}] patient list {} retrieval failed", institutions.size(), patientListRequestDto, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @GetMapping("/institutions/{institutionId}/patients/{patientId}")
    public ResponseEntity<ResponseDto> getPatient(@PathVariable Integer institutionId,
                                                  @PathVariable Integer patientId) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        try {
            PatientResponseDto patientDataDto = patientService.getPatient(institution, patientId);
            log.info("ADMIN [{}] patientId: {} retrieved", institution.getName(), patientId);
            return ResponseEntity.ok(patientDataDto);
        } catch (Exception ex) {
            log.error("ADMIN [{}] patientId: {} failed to retrieve", institution.getName(), patientId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @DeleteMapping("/institutions/{institutionId}/patients/{patientId}")
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


    @PutMapping("/institutions/{institutionId}/patients/{patientId}")
    public ResponseEntity<ErrorResponseDto> restoreDeletedPatient(@PathVariable Integer institutionId,
                                                                  @PathVariable Integer patientId) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        try {
            patientService.restorePatient(institution, patientId);
            log.info("ADMIN [{}] patient Id {} restored", institution.getName(), patientId);
            return ResponseEntity.ok(new ErrorResponseDto("successfully recovered deleted patient"));
        } catch (Exception ex) {
            log.error("ADMIN [{}] patient Id {} restored failed", institution.getName(), patientId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @GetMapping("/institutions/{institutionId}/patients/{patientId}/profile")
    public ResponseEntity<PatientProfileResponseDto> getPatientProfiles(
            @PathVariable("institutionId") int institutionId,
            @PathVariable("patientId") int patientId) {

        Institution institution = institutionService.getInstitutionById(institutionId);
        log.info("[{}] patientId: {} Received request to get patient profile list",
                institution.getName(), patientId);
        PatientProfileResponseDto patientProfileResponseDto =
                patientProfileService.getPatientProfilesByPatientIdAndInstitution(institution, patientId);
        return ResponseEntity.ok(patientProfileResponseDto);
    }

    @GetMapping("/institutions/{institutionId}/patients/{patientId}/reports")
    public ResponseEntity<?> getReportByPatient(
            @PathVariable int institutionId,
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
            @RequestParam(required = false) Integer linkStatus) {
        Institution institution = institutionService.getInstitutionById(institutionId);
        AiResult status = null;
        if (aiResultStr != null)
            status = AiResult.fromLabel(aiResultStr);
        ReportsListRequestDto reportsListRequestDto =
                new ReportsListRequestDto(query, startDate, endDate, page, size,sortBy, sortDir, sex, status, linkStatus);
        log.info("[{}] patientId: {} Received request to retrieve report", institution.getName(), patientId);
        try {
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
            var reportDetailResponseDtos = reportsService.getReportsByPatient(institution, patientId, reportsListRequestDto, pageable);
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

}

package org.cataract.web.presentation.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.PatientService;
import org.cataract.web.application.service.UserService;
import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.ResponseDto;
import org.cataract.web.presentation.dto.requests.CreatePatientRequestDto;
import org.cataract.web.presentation.dto.requests.PatientListRequestDto;
import org.cataract.web.presentation.dto.requests.UpdatePatientRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.FullPatientDataDto;
import org.cataract.web.presentation.dto.responses.OffsetPaginationResult;
import org.cataract.web.presentation.dto.responses.PatientResponseDto;
import org.hibernate.validator.constraints.Range;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/patients")
@Validated
@Slf4j
public class PatientController {

    private final PatientService patientService;
    private final UserService userService;

    private static final int DEFAULT_NUM_PATIENT_IMAGE_DATA = 3;

    public PatientController(PatientService patientService,
                             UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getPatientList(
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
                    regexp = "^(name|dateOfBirth|createdAt|updatedAt)$",
                    message = "only accepts 'name', 'dateOfBirth', 'createdAt', or 'updatedAt' as 'sortBy'"
            )
            @RequestParam(required = false) String sortBy,
            @Pattern(
                    regexp = "^(asc|desc)$",
                    message = "only accepts 'asc' or 'desc' as 'sortDir'"
            )
            @RequestParam(required = false) String sortDir,
            @Range(min=0, max=1, message = "dataStatus is either 0 or 1")
            @RequestParam(required = false) Integer dataStatus,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirthFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirthTo,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        PatientListRequestDto patientListRequestDto =
                new PatientListRequestDto(query, startDate, endDate, page, size,sortBy, sortDir, dataStatus, sex,
                        dateOfBirthFrom, dateOfBirthTo);
        log.info("[{}] Received request to get patient list {} with query {}", institution.getName(), patientListRequestDto, query);
        try {
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
            Object patientResponseDtoList = patientService.getPatientsByInstitution(institution, patientListRequestDto, pageable);
            log.info("[{}] patient list {} retrieval success", institution.getName(), patientListRequestDto);
            if (pageable.isPaged()) {
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<PatientResponseDto>)patientResponseDtoList));
            }
            return ResponseEntity.ok(patientResponseDtoList);
        } catch (Exception ex) {
            log.warn("[{}] patient list {} retrieval failed", institution.getName(), patientListRequestDto, ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> getPatientListAsCsv(
            @Size(max=30, message = "query must be smaller than 30 characters")
            @RequestParam(value = "q", required = false) String query,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate startDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate endDate,
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
            @Range(min=0, max=1, message = "dataStatus is either 0 or 1")
            @RequestParam(required = false) Integer dataStatus,
            @Pattern(
                    regexp = "^(male|female|other)$",
                    message = "Sex must be 'male', 'female', or 'other'"
            )
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirthFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirthTo,
            Authentication authentication) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        PatientListRequestDto patientListRequestDto =
                new PatientListRequestDto(query, startDate, endDate, sortBy, sortDir, dataStatus, sex,
                        dateOfBirthFrom, dateOfBirthTo);
        log.info("[{}] Received request to get patient list {} with query {}", institution.getName(), patientListRequestDto, query);
        try {
            ByteArrayInputStream patientResponseDtoCsvList = patientService.getPatientsListByUserInstitutionAsCsv(institution, patientListRequestDto);
            log.info("[{}] patient list {} retrieval success", institution.getName(), patientListRequestDto);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patientList.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(new InputStreamResource(patientResponseDtoCsvList));
        } catch (Exception ex) {
            log.warn("[{}] patient list {} retrieval failed", institution.getName(), patientListRequestDto, ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(Authentication authentication,
                                                            @RequestBody @Valid
                                                            CreatePatientRequestDto createPatientRequestDto) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] Received request to create patient {}", institution.getName(), createPatientRequestDto);
        PatientResponseDto responseDto = patientService.createPatient(institution, createPatientRequestDto);
        log.info("[{}] Patient successfully added", institution.getName());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponseDto> getPatient(Authentication authentication, @PathVariable Integer patientId) {

        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] patientId: {} Request to get patient info", institution.getName(), patientId);
        PatientResponseDto patientResponseDto = patientService.getPatient(institution, patientId);
        return ResponseEntity.ok(patientResponseDto);
    }

    @GetMapping("/data/{patientId}")
    public ResponseEntity<FullPatientDataDto> getFullPatientData(Authentication authentication, @PathVariable Integer patientId) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] patientId: {} Request to fetch patient full info", institution.getName(), patientId);
        FullPatientDataDto fullPatientDataDto = patientService.getFullPatientData(institution, patientId,
                DEFAULT_NUM_PATIENT_IMAGE_DATA);
        return ResponseEntity.ok(fullPatientDataDto);
    }

    @PutMapping("/{patientId}")
    public ResponseEntity<PatientResponseDto> updatePatient(Authentication authentication, @PathVariable Integer patientId,
                                                            @RequestBody UpdatePatientRequestDto updatePatientRequestDto) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] patientId: {} Request to update patient info", institution.getName(), patientId);
        PatientResponseDto patientResponseDto = patientService.updatePatient(institution, patientId, updatePatientRequestDto);
        log.info("[{}] patientId: {} updated successfully", institution.getName(), patientId);
        return ResponseEntity.ok(patientResponseDto);
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<ErrorResponseDto> deletePatient(Authentication authentication, @PathVariable Integer patientId) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] patientId: {} Request to delete ", institution.getName(), patientId);
        patientService.deletePatient(institution, patientId);
        return ResponseEntity.ok(new ErrorResponseDto("patient successfully deleted"));
    }
}
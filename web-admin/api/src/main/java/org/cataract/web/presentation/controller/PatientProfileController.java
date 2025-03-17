package org.cataract.web.presentation.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.PatientProfileService;
import org.cataract.web.application.service.UserService;
import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.requests.CreateProfileRequestDto;
import org.cataract.web.presentation.dto.requests.UpdateProfileRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.PatientProfileResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@Validated
@Slf4j
public class PatientProfileController {

    private final PatientProfileService patientProfileService;
    private final UserService userService;

    public PatientProfileController(PatientProfileService patientProfileService,
                                    UserService userService) {
        this.patientProfileService = patientProfileService;
        this.userService = userService;
    }

    @GetMapping("/{patientId}/profile")
    public ResponseEntity<PatientProfileResponseDto> getPatientProfiles(
            Authentication authentication,
            @PathVariable("patientId") int patientId) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] patientId: {} Received request to get patient profile list",
                institution.getName(), patientId);
        PatientProfileResponseDto patientProfileResponseDto = patientProfileService.getPatientProfilesByPatientIdAndInstitution(institution, patientId);
        return ResponseEntity.ok(patientProfileResponseDto);
    }

    @PostMapping("/{patientId}/profile")
    public ResponseEntity<PatientProfileResponseDto> createPatientProfile(
            Authentication authentication,
            @PathVariable("patientId") int patientId,
            @RequestBody @Valid CreateProfileRequestDto createProfileRequestDto){
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] patientId: {} Received request to create patient profile {}", institution.getName(), patientId, createProfileRequestDto);
        PatientProfileResponseDto patientProfileResponseDto = patientProfileService.createPatientProfile(institution, patientId, createProfileRequestDto);
        return ResponseEntity.ok(patientProfileResponseDto);
    }

    @PutMapping("/{patientId}/profile")
    public ResponseEntity<PatientProfileResponseDto> updatePatientProfile(Authentication authentication,
                                                                          @PathVariable int patientId,
                                                                          @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] patientId: {} Received request to update profile",
                institution.getName(), updateProfileRequestDto.getPatientId());
        PatientProfileResponseDto patientProfileResponseDto = patientProfileService.updateProfile(institution, patientId, updateProfileRequestDto);
        return ResponseEntity.ok(patientProfileResponseDto);
    }

    @DeleteMapping("/{patientId}/profile")
    public ResponseEntity<ErrorResponseDto> deletePatientProfile(Authentication authentication, @PathVariable int patientId) {
        String username = authentication.getName();
        Institution institution = userService.getInstitution(username);
        log.info("[{}] patientId: {} Received request to delete patient profile", institution.getName(), patientId);
        patientProfileService.deletePatientProfile(institution, patientId);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("patient profile health record deleted");
        return ResponseEntity.ok(errorResponseDto);
    }
}
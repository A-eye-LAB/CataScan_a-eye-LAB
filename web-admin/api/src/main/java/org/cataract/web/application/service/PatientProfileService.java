package org.cataract.web.application.service;

import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.requests.CreateProfileRequestDto;
import org.cataract.web.presentation.dto.requests.UpdateProfileRequestDto;
import org.cataract.web.presentation.dto.responses.PatientProfileResponseDto;

public interface PatientProfileService {

    PatientProfileResponseDto updateProfile(Institution institution, int patientId, UpdateProfileRequestDto updateProfileRequestDto);

    void deletePatientProfile(Institution institution, int patientId);

    PatientProfileResponseDto getPatientProfilesByPatientIdAndInstitution(Institution institution, int patientId);

    PatientProfileResponseDto createPatientProfile(Institution institution, int patientId, CreateProfileRequestDto createProfileRequestDto);
}
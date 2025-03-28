package org.cataract.web.application.service;

import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.requests.CreatePatientRequestDto;
import org.cataract.web.presentation.dto.requests.PatientListRequestDto;
import org.cataract.web.presentation.dto.requests.UpdatePatientRequestDto;
import org.cataract.web.presentation.dto.responses.FullPatientDataDto;
import org.cataract.web.presentation.dto.responses.PatientResponseDto;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;

public interface PatientService {

    PatientResponseDto createPatient(Institution institution, CreatePatientRequestDto createPatientRequestDto);

    PatientResponseDto updatePatient(Institution institution, Integer patientId, UpdatePatientRequestDto updatePatientRequestDto);
    void deletePatient(Institution institution, Integer patientId);

    Object getPatientsByInstitution(Institution institution, PatientListRequestDto patientListRequestDto, Pageable pageable);

    FullPatientDataDto getFullPatientData(Institution institution, Integer patientId, int numOfImages);

    PatientResponseDto getPatient(Institution institution, Integer patientId);

    ByteArrayInputStream getPatientsListByUserInstitutionAsCsv(Institution institution, PatientListRequestDto patientListRequestDto);

    PatientResponseDto restorePatient(Institution institution, Integer patientId);

    void deletePatientAndDataPermanently(Institution institution, Integer patientId);

    int deletePatientByPolicy(int retentionDays);
}

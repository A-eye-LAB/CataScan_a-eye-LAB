package org.cataract.web.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.PatientProfileService;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.Patient;
import org.cataract.web.domain.PatientProfiles;
import org.cataract.web.domain.exception.PatientNotFoundException;
import org.cataract.web.domain.exception.ProfileNotFoundException;
import org.cataract.web.infra.PatientProfileRepository;
import org.cataract.web.infra.PatientRepository;
import org.cataract.web.presentation.dto.requests.CreateProfileRequestDto;
import org.cataract.web.presentation.dto.requests.UpdateProfileRequestDto;
import org.cataract.web.presentation.dto.responses.PatientProfileResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientProfileServiceImpl implements PatientProfileService {

    private final PatientProfileRepository patientProfileRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public PatientProfileResponseDto updateProfile(Institution institution, int patientId, UpdateProfileRequestDto updateProfileRequestDto) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        PatientProfiles existingPatientProfiles =
                patientProfileRepository.findByPatient(patient).orElseThrow(ProfileNotFoundException::new);
        updateProfileRequestDto.setPatientId(patientId);
        PatientProfiles updatedPatientProfiles = new PatientProfiles(updateProfileRequestDto);
        updatedPatientProfiles.setPatient(patient);
        updatedPatientProfiles.setProfileId(existingPatientProfiles.getProfileId());
        updatedPatientProfiles = patientProfileRepository.save(updatedPatientProfiles);
        log.debug("[{}] updated patient {} {} profile", institution.getName(), patient.getName(), patientId);
        return PatientProfileResponseDto.toDto(updatedPatientProfiles);
    }

    @Transactional
    public void deletePatientProfile(Institution institution, int patientId) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        PatientProfiles patientProfiles =
                patientProfileRepository.findByPatient(patient).orElseThrow(ProfileNotFoundException::new);

        log.debug("[{}] updated patient {}", institution.getName(), patientId);
        patientProfileRepository.delete(patientProfiles);
    }

    public PatientProfileResponseDto getPatientProfilesByPatientIdAndInstitution(Institution institution, int patientId) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        Optional<PatientProfiles> optionalProfilePage = patientProfileRepository.findByPatient(patient);
        PatientProfiles patientProfiles = optionalProfilePage.orElse(new PatientProfiles());
        log.info("found Patient Profile for patientId : {}, profileId : {}", patientId, patientProfiles.getProfileId());
        return PatientProfileResponseDto.toDto(patientProfiles);
    }
    
    @Transactional
    public PatientProfileResponseDto createPatientProfile(Institution institution, int patientId, CreateProfileRequestDto createProfileRequestDto) {

        Patient patient = patientRepository.findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(patientId, institution, 1)
                .orElseThrow(PatientNotFoundException::new);

        PatientProfiles patientProfiles = new PatientProfiles(createProfileRequestDto);
        patientProfiles.setPatient(patient);
        PatientProfiles savedPatientProfiles = patientProfileRepository.save(patientProfiles);
        log.debug("[{}] created patient profile of patient Id {} as profileId {}", institution.getName(), patientId, savedPatientProfiles.getProfileId());
        return PatientProfileResponseDto.toDto(savedPatientProfiles);

    }

}

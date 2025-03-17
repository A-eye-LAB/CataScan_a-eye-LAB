package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.PatientHealthInfo;
import org.cataract.web.domain.PatientProfiles;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class PatientProfileResponseDto implements ResponseDto {

    long profileId = 0L;
    PatientHealthInfo healthInfo;
    String remarks;

    public static PatientProfileResponseDto toDto(PatientProfiles patientProfiles) {
        PatientProfileResponseDto patientProfileResponseDto = new PatientProfileResponseDto();
        patientProfileResponseDto.profileId = patientProfiles.getProfileId();
        if (patientProfiles.getProfileId() != null) {
            patientProfileResponseDto.healthInfo = PatientHealthInfo.parse(patientProfiles.getAdditionalMedicalInfo());
        }
        if (patientProfileResponseDto.healthInfo == null)
            patientProfileResponseDto.healthInfo = new PatientHealthInfo();
        patientProfileResponseDto.remarks = patientProfiles.getRemarks();
        return patientProfileResponseDto;
    }

    public PatientProfileResponseDto() {
        this.healthInfo = new PatientHealthInfo();
    }
}

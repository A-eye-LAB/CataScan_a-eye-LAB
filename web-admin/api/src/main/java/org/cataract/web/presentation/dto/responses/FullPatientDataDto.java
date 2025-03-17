package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Patient;
import org.cataract.web.domain.PatientHealthInfo;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.presentation.dto.ResponseDto;

import java.util.List;

@Getter
public class FullPatientDataDto implements ResponseDto {

    String patientName;

    String dateOfBirth;

    int age;

    String phoneNumber;

    PatientHealthInfo healthInfo;

    List<PatientReportResponseDto> scanResults;

    String remarks;

    public static FullPatientDataDto toDto(Patient patient, PatientProfileResponseDto patientProfile, List<PatientReportResponseDto> patientReports) {
        FullPatientDataDto fullPatientDataDto = new FullPatientDataDto();
        fullPatientDataDto.patientName = patient.getName();
        fullPatientDataDto.dateOfBirth = DateFormatHelper.date2StringSep(patient.getDateOfBirth());
        fullPatientDataDto.age = DateFormatHelper.calculateAge(patient.getDateOfBirth());
        fullPatientDataDto.phoneNumber = patient.getPhoneNum();
        PatientHealthInfo patientHealthInfo = PatientHealthInfo.parse(patientProfile.toString());
        if (patientHealthInfo == null)
            fullPatientDataDto.healthInfo = new PatientHealthInfo();
        fullPatientDataDto.scanResults = patientReports;
        fullPatientDataDto.remarks = patientProfile.getRemarks();
        return fullPatientDataDto;
    }


}

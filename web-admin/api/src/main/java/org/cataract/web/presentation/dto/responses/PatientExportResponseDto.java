package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Patient;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class PatientExportResponseDto implements ResponseDto {

    private String patientName;
    private String sex;
    private String dateOfBirth;
    private int age;
    private String phoneNumber;

    public static PatientExportResponseDto toDto(Patient patient) {

        PatientExportResponseDto patientExportResponseDto = new PatientExportResponseDto();
        patientExportResponseDto.patientName = patient.getName();
        patientExportResponseDto.sex = patient.getSex();
        patientExportResponseDto.dateOfBirth = DateFormatHelper.date2StringSep(patient.getDateOfBirth());
        patientExportResponseDto.age = DateFormatHelper.calculateAge(patient.getDateOfBirth());
        patientExportResponseDto.phoneNumber = patient.getPhoneNum();
        return patientExportResponseDto;

    }

}

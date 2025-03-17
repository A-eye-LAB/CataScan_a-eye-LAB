package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Patient;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class PatientResponseDto implements ResponseDto {

    private String institutionName;
    private Integer patientId;
    private String name;
    private String sex;
    private String dateOfBirth;
    private String phoneNum;
    private int age;
    private String registrationDate;
    private String remarks;


    public static PatientResponseDto toDto(Patient patient) {

        PatientResponseDto patientResponseDto = new PatientResponseDto();
        patientResponseDto.patientId = patient.getPatientId();
        patientResponseDto.name = patient.getName();
        patientResponseDto.sex = patient.getSex();
        patientResponseDto.dateOfBirth = DateFormatHelper.date2StringSep(patient.getDateOfBirth());
        patientResponseDto.phoneNum = patient.getPhoneNum();
        patientResponseDto.age = DateFormatHelper.calculateAge(patient.getDateOfBirth());
        patientResponseDto.institutionName = patient.getInstitution().getName();
        patientResponseDto.registrationDate = DateFormatHelper.date2StringSep(patient.getRegistrationDate());
        return patientResponseDto;

    }
}

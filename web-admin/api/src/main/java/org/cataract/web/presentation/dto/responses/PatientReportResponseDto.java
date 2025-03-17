package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.domain.Reports;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class PatientReportResponseDto implements ResponseDto {

    Long reportId;
    String leftEyeImagePath;
    String rightEyeImagePath;
    String scanDate;
    String leftAiResult;
    String rightAiResult;
    String leftEyeDiagnosis;
    String rightEyeDiagnosis;
    String leftEyeRemarks;
    String rightEyeRemarks;


    public static PatientReportResponseDto toDto(Reports reports) {
        PatientReportResponseDto patientReportResponseDto = new PatientReportResponseDto();
        patientReportResponseDto.reportId = reports.getReportId();
        patientReportResponseDto.leftEyeImagePath = reports.getLImagePath();
        patientReportResponseDto.rightEyeImagePath = reports.getRImagePath();
        patientReportResponseDto.scanDate = DateFormatHelper.datetime2String(reports.getScanDate());
        patientReportResponseDto.leftAiResult = reports.getLAiResult();
        patientReportResponseDto.rightAiResult = reports.getRAiResult();
        patientReportResponseDto.leftEyeDiagnosis = reports.getLDiagnosis();
        patientReportResponseDto.rightEyeDiagnosis = reports.getLDiagnosis();
        patientReportResponseDto.leftEyeRemarks = reports.getLRemark();
        patientReportResponseDto.rightEyeRemarks = reports.getRRemark();
        return patientReportResponseDto;
    }

    public PatientReportResponseDto() {
        this.reportId = 0L;
        this.leftEyeImagePath = "";
        this.rightEyeImagePath = "";
        this.scanDate = "";
        this.leftAiResult = "";
        this.rightAiResult = "";
        this.leftEyeDiagnosis = "";
        this.rightEyeDiagnosis = "";
        this.leftEyeRemarks = "";
        this.rightEyeRemarks = "";
    }

}

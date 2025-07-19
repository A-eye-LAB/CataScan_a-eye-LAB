package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Report;
import org.cataract.web.helper.DateFormatHelper;
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


    public static PatientReportResponseDto toDto(Report report) {
        PatientReportResponseDto patientReportResponseDto = new PatientReportResponseDto();
        patientReportResponseDto.reportId = report.getReportId();
        patientReportResponseDto.leftEyeImagePath = report.getLImagePath();
        patientReportResponseDto.rightEyeImagePath = report.getRImagePath();
        patientReportResponseDto.scanDate = DateFormatHelper.datetime2String(report.getScanDate());
        patientReportResponseDto.leftAiResult = report.getLAiResult();
        patientReportResponseDto.rightAiResult = report.getRAiResult();
        patientReportResponseDto.leftEyeDiagnosis = report.getLDiagnosis();
        patientReportResponseDto.rightEyeDiagnosis = report.getLDiagnosis();
        patientReportResponseDto.leftEyeRemarks = report.getLRemark();
        patientReportResponseDto.rightEyeRemarks = report.getRRemark();
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

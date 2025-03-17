package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.domain.Reports;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class ReportDetailResponseDto implements ResponseDto  {

    Long reportId;
    String scanDate;
    String leftAiResult;
    String rightAiResult;
    int linkStatus;
    String patientName;
    String leftEyeImageFilePath;
    String rightEyeImageFilePath;
    String leftEyeDiagnosis;
    String rightEyeDiagnosis;
    String leftEyeRemarks;
    String rightEyeRemarks;
    String comments;

    public static ReportDetailResponseDto toDto(Reports reports) {
        ReportDetailResponseDto reportDetailResponseDto = new ReportDetailResponseDto();
        reportDetailResponseDto.reportId = reports.getReportId();
        reportDetailResponseDto.scanDate = DateFormatHelper.datetime2String(reports.getScanDate());
        reportDetailResponseDto.leftAiResult = reports.getLAiResult();
        reportDetailResponseDto.rightAiResult = reports.getRAiResult();
        reportDetailResponseDto.linkStatus = 1;
        if (reports.getPatient() == null) {
            reportDetailResponseDto.linkStatus = 0;
            reportDetailResponseDto.patientName = reports.getImageIdentifier().split("=")[0];
        }
        else {
            reportDetailResponseDto.patientName = reports.getPatient().getName();
        }
        reportDetailResponseDto.leftEyeImageFilePath = reports.getLImagePath();
        reportDetailResponseDto.rightEyeImageFilePath = reports.getRImagePath();
        reportDetailResponseDto.leftEyeDiagnosis = reports.getLDiagnosis();
        reportDetailResponseDto.rightEyeDiagnosis = reports.getRDiagnosis();
        reportDetailResponseDto.leftEyeRemarks = reports.getLRemark();
        reportDetailResponseDto.rightEyeRemarks = reports.getRRemark();
        reportDetailResponseDto.comments = reports.getComments();
        return reportDetailResponseDto;
    }

}

package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Report;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class ReportDetailResponseDto implements ResponseDto  {

    Long reportId;
    String scanDate;
    String leftAiResult;
    String rightAiResult;
    int linkStatus;
    String patientName;
    String sex;
    String leftEyeImageFilePath;
    String rightEyeImageFilePath;
    String leftEyeDiagnosis;
    String rightEyeDiagnosis;
    String leftEyeRemarks;
    String rightEyeRemarks;
    String comments;

    public static ReportDetailResponseDto toDto(Report report) {
        ReportDetailResponseDto reportDetailResponseDto = new ReportDetailResponseDto();
        reportDetailResponseDto.reportId = report.getReportId();
        reportDetailResponseDto.scanDate = DateFormatHelper.datetime2String(report.getScanDate());
        reportDetailResponseDto.leftAiResult = report.getLAiResult();
        reportDetailResponseDto.rightAiResult = report.getRAiResult();
        reportDetailResponseDto.linkStatus = 1;
        if (report.getPatient() == null) {
            reportDetailResponseDto.linkStatus = 0;
            reportDetailResponseDto.patientName = report.getImageIdentifier().split("=")[0];
            switch (report.getImageIdentifier().split("=")[1]) {
                case "m" -> reportDetailResponseDto.sex = "male";
                case "f" -> reportDetailResponseDto.sex = "female";
                default ->  reportDetailResponseDto.sex = "other";
            }
        }
        else {
            reportDetailResponseDto.patientName = report.getPatient().getName();
            reportDetailResponseDto.sex = report.getPatient().getSex();
        }
        reportDetailResponseDto.leftEyeImageFilePath = report.getLImagePath();
        reportDetailResponseDto.rightEyeImageFilePath = report.getRImagePath();
        reportDetailResponseDto.leftEyeDiagnosis = report.getLDiagnosis();
        reportDetailResponseDto.rightEyeDiagnosis = report.getRDiagnosis();
        reportDetailResponseDto.leftEyeRemarks = report.getLRemark();
        reportDetailResponseDto.rightEyeRemarks = report.getRRemark();
        reportDetailResponseDto.comments = report.getComments();
        return reportDetailResponseDto;
    }

}
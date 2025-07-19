package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Report;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class ReportSimpleResponseDto implements ResponseDto {

    Long reportId;
    String institutionName;
    String patientName;
    String scanDate;
    String leftAiResult;
    String rightAiResult;
    String sex;
    int linkStatus;

    public static ReportSimpleResponseDto toDto(Report report) {
        ReportSimpleResponseDto reportSimpleResponseDto = new ReportSimpleResponseDto();
        reportSimpleResponseDto.reportId = report.getReportId();
        reportSimpleResponseDto.institutionName = report.getInstitution().getName();
        reportSimpleResponseDto.scanDate = DateFormatHelper.datetime2String(report.getScanDate());
        reportSimpleResponseDto.leftAiResult = report.getLAiResult();
        reportSimpleResponseDto.rightAiResult = report.getRAiResult();
        reportSimpleResponseDto.linkStatus = 0;
        if (report.getPatient() == null) {
            String[] identifier = report.getImageIdentifier().split("=");
            reportSimpleResponseDto.patientName = identifier[0];
            reportSimpleResponseDto.sex = identifier[1];
        } else {
            reportSimpleResponseDto.linkStatus = 1;
            reportSimpleResponseDto.patientName = report.getPatient().getName();
            reportSimpleResponseDto.sex = report.getPatient().getSex();
        }
        return reportSimpleResponseDto;
    }
}

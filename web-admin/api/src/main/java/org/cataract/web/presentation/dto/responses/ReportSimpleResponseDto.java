package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.domain.Reports;
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

    public static ReportSimpleResponseDto toDto(Reports reports) {
        ReportSimpleResponseDto reportSimpleResponseDto = new ReportSimpleResponseDto();
        reportSimpleResponseDto.reportId = reports.getReportId();
        reportSimpleResponseDto.institutionName = reports.getInstitution().getName();
        reportSimpleResponseDto.scanDate = DateFormatHelper.datetime2String(reports.getScanDate());
        reportSimpleResponseDto.leftAiResult = reports.getLAiResult();
        reportSimpleResponseDto.rightAiResult = reports.getRAiResult();
        reportSimpleResponseDto.linkStatus = 0;
        if (reports.getPatient() == null) {
            String[] identifier = reports.getImageIdentifier().split("=");
            reportSimpleResponseDto.patientName = identifier[0];
            reportSimpleResponseDto.sex = identifier[1];
        } else {
            reportSimpleResponseDto.linkStatus = 1;
            reportSimpleResponseDto.patientName = reports.getPatient().getName();
            reportSimpleResponseDto.sex = reports.getPatient().getSex();
        }
        return reportSimpleResponseDto;
    }
}

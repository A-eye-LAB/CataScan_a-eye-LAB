package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.domain.Reports;
import org.cataract.web.presentation.dto.ResponseDto;

import java.util.Date;

@Getter
public class ReportResponseDto implements ResponseDto {
    String reportId;
    String leftImageFilePath;
    String rightImageFilePath;
    String scanDate;
    String leftAiResult;
    String rightAiResult;
    String imageIdentifier;

    public static ReportResponseDto toDto(Reports reports) {
        ReportResponseDto reportResponseDto = new ReportResponseDto();
        reportResponseDto.reportId = new StringBuilder(reports.getInstitution().getName()).append("-")
                .append(DateFormatHelper.date2StringWithoutSep(new Date())).append("-").append(reports.getReportId()).toString();
        reportResponseDto.leftImageFilePath = reports.getLImagePath();
        reportResponseDto.rightImageFilePath = reports.getRImagePath();
        reportResponseDto.scanDate = DateFormatHelper.datetime2String(reports.getScanDate());
        reportResponseDto.leftAiResult = reports.getLAiResult();
        reportResponseDto.rightAiResult = reports.getRAiResult();
        reportResponseDto.imageIdentifier = reports.getImageIdentifier();
        return reportResponseDto;
    }

}

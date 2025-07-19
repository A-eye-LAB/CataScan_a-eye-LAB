package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Report;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.presentation.dto.ResponseDto;

import java.util.Date;

@Getter
public class ReportResponseDto implements ResponseDto {
    Long reportId;
    String leftImageFilePath;
    String rightImageFilePath;
    String scanDate;
    String leftAiResult;
    String rightAiResult;
    String imageIdentifier;
    String comments;

    public static ReportResponseDto toDto(Report report) {
        ReportResponseDto reportResponseDto = new ReportResponseDto();
        reportResponseDto.reportId = report.getReportId();
        reportResponseDto.leftImageFilePath = report.getLImagePath();
        reportResponseDto.rightImageFilePath = report.getRImagePath();
        reportResponseDto.scanDate = DateFormatHelper.datetime2String(report.getScanDate());
        reportResponseDto.leftAiResult = report.getLAiResult();
        reportResponseDto.rightAiResult = report.getRAiResult();
        reportResponseDto.imageIdentifier = report.getImageIdentifier();
        reportResponseDto.comments = report.getComments();
        return reportResponseDto;
    }

}

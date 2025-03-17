package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class ReportCommentResponseDto implements ResponseDto {

    Long reportId;
    String comments;

    public static ReportCommentResponseDto toDto(Long reportId, String reportComment) {
        ReportCommentResponseDto reportCommentResponseDto = new ReportCommentResponseDto();
        reportCommentResponseDto.reportId = reportId;
        reportCommentResponseDto.comments = reportComment;
        return reportCommentResponseDto;

    }
}

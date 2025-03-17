package org.cataract.web.presentation.dto.requests;

import lombok.Getter;
import lombok.Setter;
import org.cataract.web.domain.AiResult;
import org.cataract.web.helper.ValidImageIdString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportRequestDto {

    MultipartFile leftImage;
    MultipartFile rightImage;
    AiResult leftAiResult;
    AiResult rightAiResult;
    @ValidImageIdString
    String imageIdentifier;
    LocalDateTime scanDate;
    String comments;

    public ReportRequestDto(MultipartFile leftImage, MultipartFile rightImage, AiResult leftAiResult, AiResult rightAiResult,
                            String imageIdentifier, String comments) {
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.leftAiResult = leftAiResult;
        this.rightAiResult = rightAiResult;
        String[] imageIdArr = imageIdentifier.split("=");
        StringBuilder sb = new StringBuilder(imageIdArr[0]).append("=");
        switch (imageIdArr[1]) {
            case "m":
                sb.append("male");
                break;
            case "f":
                sb.append("female");
                break;
            default:
                sb.append("other");
        }
        this.imageIdentifier = sb.toString();
        this.comments = comments;
    }
}

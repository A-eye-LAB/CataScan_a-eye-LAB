package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class ErrorResponseDto implements ResponseDto {

    private final String message;

    public ErrorResponseDto(String message) {
        this.message = message;
    }

    public ErrorResponseDto(Exception e){
        this.message = e.getMessage();
    }

}

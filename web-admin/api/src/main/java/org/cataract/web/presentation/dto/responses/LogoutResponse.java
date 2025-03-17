package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class LogoutResponse implements ResponseDto {

    String message;

    public LogoutResponse(String message) {
        this.message = message;
    }
}

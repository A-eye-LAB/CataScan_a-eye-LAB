package org.cataract.web.presentation.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cataract.web.presentation.dto.ResponseDto;

@Setter
@Getter
@AllArgsConstructor
public class TokenResponse implements ResponseDto {
    private String token;

}

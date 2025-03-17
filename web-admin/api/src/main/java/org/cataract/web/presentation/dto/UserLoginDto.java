package org.cataract.web.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserLoginDto {

    @Size(min=4, max=50, message = "username must be between 4 and 50")
    @NotBlank
    String username;
    @NotBlank(message = "password should not be empty")
    String password;

}

package org.cataract.web.presentation.dto.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateUserRequestDto {

    @Nullable
    @Size(max = 50, message = "username must not exceed 50 characters")
    private String username;

    @Nullable
    @Size(min=8, max = 32, message = "Password must be in the range of 8-32 characters")
    private String password;

    @Nullable
    @Email(message = "Email should be valid")
    private String email;

    @Nullable
    @Size(min=1, max=100)
    private String institutionName;

}

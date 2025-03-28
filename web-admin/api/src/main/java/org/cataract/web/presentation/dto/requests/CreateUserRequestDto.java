package org.cataract.web.presentation.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDto {

    @NotBlank(message = "Username is mandatory")
    @Size(max = 20, message = "Username must not exceed 20 characters")
    String username;

    @Size(min=8, max = 32, message = "Password must be in the range of 8-32 characters")
    String password;

    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Institution name is mandatory")
    String institutionName;
}

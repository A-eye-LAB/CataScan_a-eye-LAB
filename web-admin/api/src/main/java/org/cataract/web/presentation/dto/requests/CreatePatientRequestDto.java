package org.cataract.web.presentation.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.cataract.web.domain.PatientHealthInfo;
import org.cataract.web.helper.ValidPastOrPresentDate;

import java.util.Date;

@Getter
public class CreatePatientRequestDto {

    @NotBlank(message = "patientName is mandatory.")
    String patientName;

    @NotBlank(message = "Sex is mandatory.")
    @Pattern(
            regexp = "^(male|female|other)$",
            message = "Sex must be 'male', 'female', or 'other'"
    )
    String sex;

    String phoneNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date registrationDate =new Date();

    @ValidPastOrPresentDate
    @NotNull(message = "Date of birth is mandatory")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date dateOfBirth;

    private String remarks;

    private PatientHealthInfo healthInfo;


}

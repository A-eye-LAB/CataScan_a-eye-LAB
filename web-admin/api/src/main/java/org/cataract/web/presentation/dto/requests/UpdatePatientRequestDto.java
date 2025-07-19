package org.cataract.web.presentation.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.cataract.web.helper.ValidPastOrPresentDate;

@Getter
public class UpdatePatientRequestDto {

    @Nullable
    String patientName;

    @Nullable
    String sex;

    @ValidPastOrPresentDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    String dateOfBirth;
    String phoneNum;

    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd")
    String registrationDate;

    Integer dataStatus = 1;

}

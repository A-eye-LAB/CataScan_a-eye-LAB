package org.cataract.web.presentation.dto.requests;

import lombok.Getter;
import org.cataract.web.domain.PatientHealthInfo;

@Getter
public class CreateProfileRequestDto {

    private String remarks;
    private PatientHealthInfo healthInfo;

}

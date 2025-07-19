package org.cataract.web.presentation.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.cataract.web.domain.PatientHealthInfo;

import java.util.Date;

@Getter
public class CreateProfileRequestDto {

    private String remarks;
    private PatientHealthInfo healthInfo;

}

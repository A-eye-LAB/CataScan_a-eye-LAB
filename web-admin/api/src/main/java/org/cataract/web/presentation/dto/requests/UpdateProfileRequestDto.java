package org.cataract.web.presentation.dto.requests;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cataract.web.domain.PatientHealthInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDto {

    @Nullable
    private String remarks;
    private PatientHealthInfo healthInfo;
    private int patientId = 0;

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public UpdateProfileRequestDto(PatientHealthInfo healthInfo) {
        this.healthInfo = healthInfo;
    }
}

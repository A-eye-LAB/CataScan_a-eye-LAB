package org.cataract.web.presentation.dto.requests;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateReportRequestDto {

    String leftEyeRemarks;
    String rightEyeRemarks;
    @Pattern(
            regexp = "^(certainlyNormal|probablyNormal|uncertain|probablyCataract|certainlyCataract)$",
            message = "only accepts 'certainlyNormal','probablyNormal','uncertain','probablyCataract','certainlyCataract' as 'leftEyeDiagnosis'"
    )
    String leftEyeDiagnosis;
    @Pattern(
            regexp = "^(certainlyNormal|probablyNormal|uncertain|probablyCataract|certainlyCataract)$",
            message = "only accepts 'certainlyNormal','probablyNormal','uncertain','probablyCataract','certainlyCataract' as 'rightEyeDiagnosis'"
    )
    String rightEyeDiagnosis;
    String comments;

}

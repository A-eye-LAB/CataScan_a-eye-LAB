package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Institution;
import org.cataract.web.helper.DateFormatHelper;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class InstitutionResponseDto implements ResponseDto {

    Integer institutionId;
    String institutionName;
    String address;
    String createdDate;


    public static InstitutionResponseDto toDto(Institution institution) {

        InstitutionResponseDto institutionResponseDto = new InstitutionResponseDto();
        institutionResponseDto.institutionName = institution.getName();
        institutionResponseDto.institutionId = institution.getInstitutionId();
        institutionResponseDto.address = institution.getAddress();
        institutionResponseDto.createdDate = DateFormatHelper.date2StringSep(institution.getCreatedAt());
        return institutionResponseDto;

    }
}

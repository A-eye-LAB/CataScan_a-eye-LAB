package org.cataract.web.application.service;

import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.responses.InstitutionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InstitutionService {

    Institution getInstitutionByName(String institutionName);

    Object getAllInstitutions(Pageable pageable);

    Institution getInstitutionById(Integer institutionId);
}

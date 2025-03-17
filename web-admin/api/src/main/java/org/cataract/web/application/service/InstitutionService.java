package org.cataract.web.application.service;

import org.cataract.web.domain.Institution;
import org.springframework.data.domain.Pageable;

public interface InstitutionService {

    Institution getInstitutionByName(String institutionName);

    Object getAllInstitutions(Pageable pageable);

    Institution getInstitutionById(Integer institutionId);
}

package org.cataract.web.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.InstitutionService;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.exception.InstitutionNotFoundException;
import org.cataract.web.infra.InstitutionRepository;
import org.cataract.web.presentation.dto.responses.InstitutionResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;

    @Override
    public Institution getInstitutionByName(String institutionName) {
        return institutionRepository.findByName(institutionName).orElseThrow(InstitutionNotFoundException::new);
    }

    @Override
    public Object getAllInstitutions(Pageable pageable) {
        log.debug("retrieving all institutions by list");
        if (pageable.isUnpaged())
            return institutionRepository.findAll().stream().map(InstitutionResponseDto::toDto).collect(Collectors.toList());
        else
            return institutionRepository.findAll(pageable).map(InstitutionResponseDto::toDto);

    }

    @Override
    public Institution getInstitutionById(Integer institutionId) {
        return institutionRepository.findById(institutionId).orElseThrow(InstitutionNotFoundException::new);
    }
}

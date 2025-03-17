package org.cataract.web.infra;

import org.cataract.web.domain.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Integer> {
    Optional<Institution> findByName(String name);
}

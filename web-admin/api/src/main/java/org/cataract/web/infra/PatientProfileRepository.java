package org.cataract.web.infra;

import org.cataract.web.domain.Patient;
import org.cataract.web.domain.PatientProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfiles, Long> {
    Optional<PatientProfiles> findByPatient(Patient patient);
}
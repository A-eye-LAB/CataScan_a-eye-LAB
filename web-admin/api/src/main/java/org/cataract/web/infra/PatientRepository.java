package org.cataract.web.infra;

import org.cataract.web.domain.Institution;
import org.cataract.web.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Page<Patient> findAllByNameContainingIgnoreCaseAndInstitution(String query, Institution institution, Pageable pageable);

    @Query("SELECT MAX(p.patientId) FROM Patient p WHERE p.institution = :institution")
    Optional<Integer> findMaxByInstitutionOrderByPatientIdDesc(Institution institution);

    @Query("SELECT p FROM Patient p WHERE p.patientId = :patientId AND p.institution = :institution AND p.dataStatus = :dataStatus")
    Optional<Patient> findByPatientIdAndInstitutionAndDataStatusGreaterThanEqual(int patientId, Institution institution, int dataStatus);

    Optional<Patient> findByPatientIdAndInstitutionAndDataStatusEquals(Integer patientId, Institution institution, int i);

    List<Patient> findAllByNameContainingIgnoreCaseAndInstitution(String patientName, Institution institution, Sort sort);

    List<Patient> findAllByInstitution(Institution institution);

    List<Patient> findByDataStatusEqualsAndUpdatedAtBefore(int dataStatus, OffsetDateTime updatedAtBefore);

}
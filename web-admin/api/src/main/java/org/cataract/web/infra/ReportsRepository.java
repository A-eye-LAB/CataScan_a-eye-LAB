package org.cataract.web.infra;

import org.cataract.web.domain.Patient;
import org.cataract.web.domain.Reports;
import org.cataract.web.domain.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Long>, JpaSpecificationExecutor<Reports> {

    Page<Reports> findByPatientAndInstitution(Patient patient, Institution institution, Pageable pageable);

    @Query(value = "SELECT COUNT(r.*) " +
            "FROM Reports r " +
            "JOIN institutions i ON r.institution_id = i.institution_id " +
            "WHERE i.institution_id = :institutionId;", nativeQuery = true)
    long countUnlinkedReports(int institutionId);

    List<Reports> findByInstitutionAndScanDateAfter(Institution institution, LocalDateTime twentyFourHoursAgo);

    List<Reports> findByPatient(Patient patient);

    @Query(value = "SELECT r.comments FROM Reports r WHERE r.reportId = :reportId")
    String findCommentsById(Long reportId);

    List<Reports> findByPatientAndInstitution(Patient patient, Institution institution);
}
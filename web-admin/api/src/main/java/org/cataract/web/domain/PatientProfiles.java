package org.cataract.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.cataract.web.presentation.dto.requests.CreatePatientRequestDto;
import org.cataract.web.presentation.dto.requests.CreateProfileRequestDto;
import org.cataract.web.presentation.dto.requests.UpdateProfileRequestDto;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="patient_profiles")
public class PatientProfiles {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long profileId;

    @Column(length = 2000)
    private String remarks;

    @Column(length = 2000)
    private String additionalMedicalInfo;

    @ManyToOne
    @JoinColumn(name = "patient_pk", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    private Date visitDate;

    public PatientProfiles(CreateProfileRequestDto createProfileRequestDto) {
        this.remarks = createProfileRequestDto.getRemarks();
        this.additionalMedicalInfo = createProfileRequestDto.getHealthInfo().toString();
        this.visitDate = createProfileRequestDto.getHealthInfo().getVisitDate();
    }

    public PatientProfiles(UpdateProfileRequestDto updateProfileRequestDto) {

        this.remarks = updateProfileRequestDto.getRemarks();
        this.additionalMedicalInfo = updateProfileRequestDto.getHealthInfo().toString();

    }

    public PatientProfiles() {
        this.remarks = "";
        this.additionalMedicalInfo = "";
        this.visitDate = new Date();
    }

    public PatientProfiles(CreatePatientRequestDto createPatientRequestDto, Patient patient) {
        this.patient = patient;
        this.institution = patient.getInstitution();
        this.remarks = createPatientRequestDto.getRemarks();
        this.additionalMedicalInfo = createPatientRequestDto.getHealthInfo().toString();
    }
}
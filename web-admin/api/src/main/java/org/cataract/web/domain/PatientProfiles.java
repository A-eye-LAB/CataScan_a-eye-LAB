package org.cataract.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.cataract.web.presentation.dto.requests.CreatePatientRequestDto;
import org.cataract.web.presentation.dto.requests.CreateProfileRequestDto;
import org.cataract.web.presentation.dto.requests.UpdateProfileRequestDto;

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
    private String healthInfo;

    @ManyToOne
    @JoinColumn(name = "patient_pk")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    private Date visitDate;

    public PatientProfiles(CreateProfileRequestDto createProfileRequestDto) {
        this.remarks = createProfileRequestDto.getRemarks();
        if (createProfileRequestDto.getHealthInfo() != null) {
            this.healthInfo = createProfileRequestDto.getHealthInfo().toString();
        }
        this.visitDate = createProfileRequestDto.getHealthInfo().getVisitDate();
    }

    public PatientProfiles(UpdateProfileRequestDto updateProfileRequestDto) {
        this.remarks = updateProfileRequestDto.getRemarks();
        if (updateProfileRequestDto.getHealthInfo() != null) {
            this.healthInfo = updateProfileRequestDto.getHealthInfo().toString();
        }
    }

    public PatientProfiles() {
        this.remarks = "";
        this.healthInfo = "";
        this.visitDate = new Date();
    }

    public PatientProfiles(CreatePatientRequestDto createPatientRequestDto, Patient patient) {
        this.patient = patient;
        this.institution = patient.getInstitution();
        this.remarks = createPatientRequestDto.getRemarks();
        if (createPatientRequestDto.getHealthInfo() != null) {
            this.healthInfo = createPatientRequestDto.getHealthInfo().toString();
        } else {
            this.healthInfo = new PatientHealthInfo().toString();
        }

    }
}
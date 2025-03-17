package org.cataract.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.cataract.web.presentation.dto.requests.CreatePatientRequestDto;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long patientPk;

    private Integer patientId;

    private String name;
    private String sex;

    private Date dateOfBirth;
    private String phoneNum;
    @ColumnDefault("1")
    private int dataStatus;
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reports> reports;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;


    public Patient(CreatePatientRequestDto createPatientRequestDto, Institution institution, Integer patientId) {

        this.patientId = patientId;
        this.name = createPatientRequestDto.getName();
        this.sex = createPatientRequestDto.getSex();
        this.dateOfBirth = createPatientRequestDto.getDateOfBirth();
        this.phoneNum = createPatientRequestDto.getPhoneNum();
        this.institution = institution;
        this.dataStatus = 1;
        this.registrationDate = LocalDate.now();

    }

    public Patient() {

    }

    public Patient(Object o, Institution institution) {
    }
}

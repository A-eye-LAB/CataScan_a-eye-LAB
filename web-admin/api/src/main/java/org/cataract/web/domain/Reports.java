package org.cataract.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.cataract.web.presentation.dto.requests.ReportRequestDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "reports")
public class Reports {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long reportId;

    @Column(length = 500)
    private String lImagePath;

    @Column(length = 500)
    private String rImagePath;

    private LocalDateTime scanDate;

    @Column(length = 50)
    private String lAiResult;

    @Column(length = 50)
    private String rAiResult;

    @Column(length = 100)
    private String lDiagnosis;

    @Column(length = 100)
    private String rDiagnosis;

    @Column(length = 2000)
    private String lRemark;

    @Column(length = 2000)
    private String rRemark;

    @Column(length = 2000)
    private String comments;


    @ManyToOne
    @JoinColumn(name = "patient_pk", nullable = true)
    private Patient patient;

    private String imageIdentifier;

    @CreationTimestamp
    @Column(name="createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    public Reports(ReportRequestDto reportRequestDto) {
        this.imageIdentifier = reportRequestDto.getImageIdentifier();
        this.scanDate = reportRequestDto.getScanDate();
        this.lAiResult = reportRequestDto.getLeftAiResult().toString();
        this.rAiResult = reportRequestDto.getRightAiResult().toString();
        this.comments = reportRequestDto.getComments();
    }

    public Reports() {

    }
}
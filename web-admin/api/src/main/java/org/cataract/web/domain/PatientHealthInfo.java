package org.cataract.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.Date;
import java.util.Optional;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientHealthInfo {

    boolean cataract;
    boolean diabetes;
    boolean hypertension;
    boolean dontKnow;
    int systolicBp;
    int diastolicBp;
    double rightEyeVision;
    double leftEyeVision;
    int bloodSugarLevel;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date visitDate;

    public static PatientHealthInfo parse(String additionalMedicalInfo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PatientHealthInfo healthInfo = objectMapper.readValue(additionalMedicalInfo, PatientHealthInfo.class);
            return healthInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PatientHealthInfo() {
        this.cataract = false;
        this.diabetes = false;
        this.hypertension = false;
        this.dontKnow = false;
        this.systolicBp = 0;
        this.diastolicBp = 0;
        this.rightEyeVision = 0;
        this.leftEyeVision = 0;
        this.bloodSugarLevel = 0;
        this.visitDate = new Date();
    }

    @Override
    public String toString() {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}

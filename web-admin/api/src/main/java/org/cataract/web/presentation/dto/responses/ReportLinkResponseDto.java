package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Reports;
import org.cataract.web.domain.Patient;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class ReportLinkResponseDto implements ResponseDto {

    int patientId;
    String patientName;
    long reportId;
    String message;

    public ReportLinkResponseDto(Patient patient, Reports reports) {

        this.patientId = patient.getPatientId();
        this.patientName = patient.getName();
        this.reportId = reports.getReportId();
        this.message = "report and patient linked successfully";
    }

    public ReportLinkResponseDto(Reports reports) {

        this.patientId = 0;
        this.patientName = "";
        this.reportId = reports.getReportId();
        this.message = "report successfully unlinked";
    }
}

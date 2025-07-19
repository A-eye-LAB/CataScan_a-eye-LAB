package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.Report;
import org.cataract.web.domain.Patient;
import org.cataract.web.presentation.dto.ResponseDto;

@Getter
public class ReportLinkResponseDto implements ResponseDto {

    int patientId;
    String patientName;
    long reportId;
    String message;

    public ReportLinkResponseDto(Patient patient, Report report) {

        this.patientId = patient.getPatientId();
        this.patientName = patient.getName();
        this.reportId = report.getReportId();
        this.message = "report and patient linked successfully";
    }

    public ReportLinkResponseDto(Report report) {

        this.patientId = 0;
        this.patientName = "";
        this.reportId = report.getReportId();
        this.message = "report successfully unlinked";
    }
}

package org.cataract.web.application.service;

import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.responses.ReportCommentResponseDto;
import org.cataract.web.presentation.dto.requests.*;
import org.cataract.web.presentation.dto.responses.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportsService {

    ReportResponseDto saveReportfromApp(Institution institution, ReportRequestDto reportRequestDto) throws Exception;

    String getExtension(String filename) ;

    ReportLinkResponseDto linkReportWithPatient(Institution institution, long imageId, int patientId);

    Object getReportsByInstitutionAndDateRange(Institution institution, ReportsListRequestDto reportsListRequestDto, Pageable pageable);

    Object getReportsByPatient(Institution institution, int patientId,
                               ReportsListRequestDto reportsListRequestDto, Pageable pageable);

    List<PatientReportResponseDto> getRecentReportByPatientIdAndInstitution(Institution institution, int patientId, int numOfReports);

    ReportDetailResponseDto getReportById(Institution institution, long reportId);

    Object getCandidatePatientsByReportId(Institution institution, long reportId, PatientListRequestDto patientListRequestDto, Pageable pageable);

    void deleteReportById(Institution institution, long reportId);

    ReportDetailResponseDto updateReport(Institution institution, long reportId, UpdateReportRequestDto updateReportRequestDto);

    long getUnlinkedReportCount(Institution institution);

    ReportLinkResponseDto unlinkReportWithPatient(Institution institution, Long reportId);

    ReportCommentResponseDto updateReportComments(Institution institution, Long reportId, ReportCommentRequestDto reportCommentRequestDto);

    ReportCommentResponseDto getReportComments(Institution institution, Long reportId);
}

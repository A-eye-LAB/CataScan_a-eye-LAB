package org.cataract.web.presentation.controller.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.InstitutionService;
import org.cataract.web.application.service.ReportsService;
import org.cataract.web.domain.AiResult;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.exception.ReportNotFoundException;
import org.cataract.web.presentation.dto.ResponseDto;
import org.cataract.web.presentation.dto.requests.ReportsListRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.OffsetPaginationResult;
import org.cataract.web.presentation.dto.responses.ReportDetailResponseDto;
import org.cataract.web.presentation.dto.responses.ReportSimpleResponseDto;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@Slf4j
public class AdminReportController {

    private final InstitutionService institutionService;

    private final ReportsService reportsService;

    @GetMapping
    public ResponseEntity<?> getReportListFromDateRange(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "institution", required = false) List<String> institutions,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) String startDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Pattern(
                    regexp = "^(name|scanDate|updatedAt)$",
                    message = "only accepts 'name','scanDate' or 'updatedAt' as 'sortBy'"
            )
            @RequestParam(required = false) String sortBy,
            @Pattern(
                    regexp = "^(asc|desc)$",
                    message = "only accepts 'asc' or 'desc' as 'sortDir'"
            )
            @RequestParam(required = false) String sortDir,
            @Pattern(
                    regexp = "^(male|female)$",
                    message = "only accepts 'male', 'female' or 'other' as 'sex'"
            )
            @RequestParam(required = false) String sex,
            @Pattern(
                    regexp = "^(lowRisk|requiresAttention|ungradable)$",
                    message = "only accepts 'lowRisk','requiresAttention' or 'ungradable' as 'status'"
            )
            @RequestParam(value = "status", required = false) String aiResultStr,
            @Range(max=1)
            @RequestParam(required = false) Integer linkStatus,
            Authentication authentication) {
        String username = authentication.getName();
        AiResult status = null;
        if (aiResultStr != null) {
            status = AiResult.fromLabel(aiResultStr);
        }
        ReportsListRequestDto reportsListRequestDto =
                new ReportsListRequestDto(query, startDate, endDate, page, size, sortBy, sortDir, sex, status, linkStatus);

        Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
        try {
            List<Institution> institutionList = new ArrayList<>();
            if (institutions != null) {
                for (String institutionName : institutions) {
                    institutionList.add(institutionService.getInstitutionByName(institutionName));
                    log.info("ADMIN [{}] Received request to get report list {} with query {}",
                            institutionName,reportsListRequestDto, query);
                }
            } else {
                log.info("ADMIN [ALL] Received request to retrieve report list by {}", reportsListRequestDto);
            }
            var reportSimpleResponseDtos = reportsService.getReportsByInstitutionAndDateRange(institutionList, reportsListRequestDto, pageable);
            log.info("ADMIN [{}] reports list retrieved successfully", institutions);
            if (pageable.isPaged()) {
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<ReportSimpleResponseDto>)reportSimpleResponseDtos));
            }
            return ResponseEntity.ok(reportSimpleResponseDtos);

        } catch (Exception ex) {
            log.error("ADMIN [{}] reports list retrieved failed", institutions, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ResponseDto> getDetailReport(@PathVariable Long reportId) {
        try {
            ReportDetailResponseDto reportDetailResponseDto = reportsService.getReportById(reportId);
            log.info("ADMIN reportId: {} retrieved by ADMIN", reportId);
            return ResponseEntity.ok(reportDetailResponseDto);
        } catch (ReportNotFoundException ex) {
            log.warn("ADMIN reportId: {} not found ADMIN", reportId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN reportId: {} failed to retrieve ADMIN", reportId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


    @DeleteMapping("/{reportId}")
    public ResponseEntity<ErrorResponseDto> deleteReport(@PathVariable Long reportId) {
        try {
            reportsService.deleteReportById(reportId);
            log.info("ADMIN reportId: {} deleted", reportId);
            return ResponseEntity.ok(new ErrorResponseDto("deleted or not found"));
        } catch (ReportNotFoundException ex) {
            log.warn("ADMIN reportId: {} not found", reportId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN reportId: {} delete failed by internal error", reportId, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }


}

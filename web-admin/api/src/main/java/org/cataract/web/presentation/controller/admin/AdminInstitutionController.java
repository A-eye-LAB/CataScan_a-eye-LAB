package org.cataract.web.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.DataDownloadService;
import org.cataract.web.application.service.InstitutionService;
import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.responses.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/institutions")
@RequiredArgsConstructor
@Slf4j
public class AdminInstitutionController {

    private final InstitutionService institutionService;

    private final DataDownloadService dataDownloadService;

    @GetMapping
    public ResponseEntity<?> getInstitutionPages(
            @RequestParam(required = false) Integer page,
            @Range(min = 1, max = 1000, message = "page size needs to be between 1 and 1000")
            @RequestParam(required = false) Integer size) {
        log.info("ADMIN Received request to retrieve institution list");
        try {
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
            var institutionResponseDtoList = institutionService.getAllInstitutions(pageable);
            log.info("ADMIN retrieved institution list successfully");
            if (pageable.isPaged()) {
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<InstitutionResponseDto>) institutionResponseDtoList));
            }
            return ResponseEntity.ok(institutionResponseDtoList);
        } catch (ClassCastException ex) {
            log.error("ADMIN institution list retrieval failed to cast page or list", ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN institution list retrieval failed", ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @PostMapping("/data")
    public ResponseEntity<byte[]> getInstitutionImageData(
            @RequestParam(required = false, name = "institutionId") List<Integer> institutionIds) {
        log.info("ADMIN Received request to retrieve institution data");
        List<Integer> institutionIdList = new ArrayList<>();
        if (institutionIds != null) {
            institutionIdList.addAll(institutionIds);
        }

        if (institutionIds == null || institutionIds.isEmpty()) {
            List<InstitutionResponseDto> institutionList = (List<InstitutionResponseDto>) institutionService.getAllInstitutions(Pageable.unpaged());
            institutionIdList.addAll(
                    institutionList.stream().map(InstitutionResponseDto::getInstitutionId).toList());
        }
        try {

            byte[] zipBytes = dataDownloadService.downloadImageDataByteArr(institutionIdList);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=institution_image_data.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @PostMapping(value = "/data2", produces = "application/zip")
    public ResponseEntity<StreamingResponseBody> getInstitutionImageData2(
            @RequestParam(required = false, name = "institutionId") List<Integer> institutionIds) {
        log.info("ADMIN Received request to retrieve institution data");
        List<Integer> institutionIdList = new ArrayList<>();
        if (institutionIds != null) {
            institutionIdList.addAll(institutionIds);
        }

        if (institutionIds == null || institutionIds.isEmpty()) {
            List<InstitutionResponseDto> institutionList = (List<InstitutionResponseDto>) institutionService.getAllInstitutions(Pageable.unpaged());
            institutionIdList.addAll(
                    institutionList.stream().map(InstitutionResponseDto::getInstitutionId).toList());
        }
        try {
            institutionIdList.clear();
            StreamingResponseBody stream = outputStream -> {
                dataDownloadService.downloadImageData(institutionIdList, outputStream);
            };
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=institution_image_data.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

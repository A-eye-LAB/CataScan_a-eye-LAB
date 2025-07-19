package org.cataract.web.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.ImageStorageService;
import org.cataract.web.presentation.dto.requests.ImageStorageCreateRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.ImageStorageResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/buckets")
@RequiredArgsConstructor
@Slf4j
public class AdminBucketController {

    private final ImageStorageService imageStorageService;

    @GetMapping
    public ResponseEntity<List<ImageStorageResponseDto>> getBucketList() {
        List<ImageStorageResponseDto> imageStorageResponseDtos = imageStorageService.getBucketList();
        return ResponseEntity.ok(imageStorageResponseDtos);
    }

    @PostMapping
    public ResponseEntity<ImageStorageResponseDto> createBucket(@RequestBody ImageStorageCreateRequestDto imageStorageCreateRequestDto)
    {
        ImageStorageResponseDto imageStorageResponseDto = imageStorageService.addBucket(imageStorageCreateRequestDto);
        return ResponseEntity.ok(imageStorageResponseDto);
    }

    @GetMapping("/{bucketId}")
    public ResponseEntity<ImageStorageResponseDto> getBucketById(@PathVariable Short bucketId) {
        try {
            ImageStorageResponseDto imageStorageResponseDto = imageStorageService.getBucketById(bucketId);
            return ResponseEntity.ok(imageStorageResponseDto);
        } catch (Exception e) {
            log.error("unable to find the bucket with id {}", bucketId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{bucketId}")
    public ResponseEntity<ErrorResponseDto> deleteImageStorageRecord(@PathVariable Short bucketId) {
        try {
            imageStorageService.deleteBucketById(bucketId);
            return ResponseEntity.ok(new ErrorResponseDto("bucket record in db deleted"));
        } catch (Exception e) {
            log.error("unable to find the bucket with id {}", bucketId);
            return ResponseEntity.notFound().build();
        }


    }
}

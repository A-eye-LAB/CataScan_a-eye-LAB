package org.cataract.web.application.service;

import org.cataract.web.domain.ImageStorage;
import org.cataract.web.presentation.dto.requests.ImageStorageCreateRequestDto;
import org.cataract.web.presentation.dto.responses.ImageStorageResponseDto;

import java.util.List;

public interface ImageStorageService {

    List<ImageStorageResponseDto> getBucketList();

    ImageStorageResponseDto addBucket(ImageStorageCreateRequestDto imageStorageCreateRequestDto);

    ImageStorage getImageStorageByBucketName(String bucketName);


    ImageStorageResponseDto getBucketById(Short bucketId);

    void deleteBucketById(Short bucketId);
}

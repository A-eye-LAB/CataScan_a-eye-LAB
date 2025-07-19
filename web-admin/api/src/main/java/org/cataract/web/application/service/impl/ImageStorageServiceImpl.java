package org.cataract.web.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.ImageStorageService;
import org.cataract.web.domain.ImageStorage;
import org.cataract.web.domain.Institution;
import org.cataract.web.infra.ImageStorageRepository;
import org.cataract.web.infra.InstitutionRepository;
import org.cataract.web.presentation.dto.requests.ImageStorageCreateRequestDto;
import org.cataract.web.presentation.dto.responses.ImageStorageResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {

    private final ImageStorageRepository imageStorageRepository;
    private final InstitutionRepository institutionRepository;

    @Value("${app.image.bucket.name}")
    String defaultImageBucketName;

    @Override
    public List<ImageStorageResponseDto> getBucketList() {
        List<ImageStorage> imageStorageList = imageStorageRepository.findAll();
        return imageStorageList.stream().map(ImageStorageResponseDto::toDto).toList();
    }

    @Override
    @Transactional
    public ImageStorageResponseDto addBucket(ImageStorageCreateRequestDto imageStorageCreateRequestDto) {
        ImageStorage imageStorage = new ImageStorage(imageStorageCreateRequestDto);
        ImageStorage savedImageStorage = imageStorageRepository.save(imageStorage);
        return ImageStorageResponseDto.toDto(savedImageStorage);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageStorage getImageStorageByBucketName(String bucketName) {
        Optional<ImageStorage> imageStorage = Optional.empty();
        if (bucketName != null)
            imageStorage = imageStorageRepository.findByBucketName(bucketName);
        if (imageStorage.isEmpty())
            imageStorage = imageStorageRepository.findByBucketName(defaultImageBucketName);
        return imageStorage.orElseThrow();

    }

    @Override
    @Transactional(readOnly = true)
    public ImageStorageResponseDto getBucketById(Short bucketId) {
        ImageStorage imageStorage = imageStorageRepository.findById(bucketId).orElseThrow();
        return ImageStorageResponseDto.toDto(imageStorage);
    }

    @Override
    @Transactional
    public void deleteBucketById(Short bucketId) {
        ImageStorage imageStorage = imageStorageRepository.findById(bucketId).orElseThrow();
        List<Institution> institutionList = imageStorage.getInstitutions();
        ImageStorage defaultImageStorage = getImageStorageByBucketName(null);
        for (Institution institution : institutionList) {
            institution.setImageStorage(defaultImageStorage);
            institutionRepository.save(institution);
            log.debug("{} images will be saved in the default bucket {} because of bucket {} deleted", institution.getName(),
                    institution.getImageStorage().getBucketName(), imageStorage.getBucketName());
        }
        imageStorageRepository.delete(imageStorage);
    }
}

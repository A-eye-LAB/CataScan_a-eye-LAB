package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cataract.web.domain.ImageStorage;

@Getter
@NoArgsConstructor
public class ImageStorageResponseDto {

    private short imageStorageId;

    private String bucketName;

    private String bucketRegion;

    public static ImageStorageResponseDto toDto(ImageStorage imageStorage) {
        ImageStorageResponseDto imageStorageResponseDto = new ImageStorageResponseDto();
        imageStorageResponseDto.imageStorageId = imageStorage.getImageStorageId();
        imageStorageResponseDto.bucketName = imageStorage.getBucketName();
        imageStorageResponseDto.bucketRegion = imageStorage.getBucketRegion();
        return imageStorageResponseDto;
    }

}

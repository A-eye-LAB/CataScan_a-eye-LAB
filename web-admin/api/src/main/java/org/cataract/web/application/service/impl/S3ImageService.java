package org.cataract.web.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.ImageService;
import org.cataract.web.domain.ImageStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@Slf4j
@Profile("prod")
public class S3ImageService implements ImageService {

    public String uploadFile(MultipartFile file, String filename, ImageStorage imageStorage) {
        String key = "uploads/" + filename;
        try (S3Client s3Client = S3Client.builder().region(Region.of(imageStorage.getBucketRegion())).build()) {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket("bms-eyelab-test").key(key).build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (Exception e) {
            log.error("error saving image for {} in the region {} bucket name {}", filename,
                    imageStorage.getBucketRegion(), imageStorage.getBucketName(), e);
        }
        return new StringBuilder("https://").append(imageStorage.getBucketName()).append(".s3.")
                .append(imageStorage.getBucketRegion()).append(".amazonaws.com/").append(key).toString();
    }

    public boolean deleteFile(String fileName, ImageStorage imageStorage) {
        try (S3Client s3Client = S3Client.builder().region(Region.of(imageStorage.getBucketRegion())).build();) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(imageStorage.getBucketName())
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("{} image file fail to be deleted", fileName);
            return false;
        }
        return true;
    }
}
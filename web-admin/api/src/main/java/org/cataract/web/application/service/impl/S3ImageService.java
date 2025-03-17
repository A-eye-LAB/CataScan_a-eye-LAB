package org.cataract.web.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.ImageService;
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
    private final S3Client s3Client;

    @Value("${app.image.bucket.name}")
    String s3BucketName;

    @Value("${app.image.bucket.region}")
    String s3BucketRegion;

    public S3ImageService() {
        this.s3Client = S3Client.builder().region(Region.AP_NORTHEAST_2).build();
    }

    public String uploadFile(MultipartFile file, String filename) throws IOException {
        String key = "uploads/" + filename;
        s3Client.putObject(
                PutObjectRequest.builder().bucket("bms-eyelab-test").key(key).build(),
                RequestBody.fromBytes(file.getBytes())
        );
        return new StringBuilder("https://").append(s3BucketName).append(".s3.")
                .append(s3BucketRegion).append(".amazonaws.com/").append(key).toString();
    }

    public boolean deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3BucketName)
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
package org.cataract.web.application.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.ImageService;
import org.cataract.web.helper.DateFormatHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Service
@Slf4j
@Profile("dev")
public class LocalImageService implements ImageService  {

    @Value("${app.image.filepath}")
    String uploadDir;

    @Value("${app.image.baseurl}")
    String imageUrlPath;

    @Value("${app.host.url}")
    String hostUrl;

    @Value("${app.host.port}")
    String hostPort;

    @PostConstruct
    private void init() {
        try {
            if (uploadDir != null && !Files.exists(Path.of(uploadDir))) {
                Files.createDirectories(Path.of(uploadDir));
            } else {
                Files.createDirectories(Path.of("/app/uploads"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory!", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String filename) throws IOException {
        String dateDirPath = File.separator + DateFormatHelper.date2StringWithoutSep(new Date()) + File.separator;
        if (!Files.exists(Path.of(uploadDir + dateDirPath)))
            Files.createDirectories(Path.of(uploadDir + dateDirPath));


        file.transferTo(Path.of(new StringBuilder(uploadDir).append(dateDirPath).append(filename).toString()));
        return new StringBuilder(hostUrl).append(":").append(hostPort).append(imageUrlPath)
                .append(dateDirPath).append(filename).toString();
    }

    @Override
    public boolean deleteFile(String fileName) {
        fileName = fileName.substring(imageUrlPath.length());
        try {
            Files.deleteIfExists(Path.of(uploadDir + File.separator + fileName));
        } catch (Exception e) {
            log.error("failed to delete file {} in local directory", fileName, e);
            return false;
        }
        return true;
    }
}
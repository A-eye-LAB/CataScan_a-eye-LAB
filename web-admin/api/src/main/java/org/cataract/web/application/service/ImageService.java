package org.cataract.web.application.service;

import org.cataract.web.domain.ImageStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String uploadFile(MultipartFile file, String filename, ImageStorage imageStorage) throws IOException;

    boolean deleteFile(String fileName, ImageStorage imageStorage);


}

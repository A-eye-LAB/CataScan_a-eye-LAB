package org.cataract.web.application.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String uploadFile(MultipartFile file, String filename) throws IOException;

    boolean deleteFile(String fileName);


}

package org.cataract.web.application.service;

import java.io.OutputStream;
import java.util.List;

public interface DataDownloadService {

    void downloadImageData(List<Integer> institutionIds, OutputStream outputStream);

    byte[] downloadImageDataByteArr(List<Integer> institutionIds);

}

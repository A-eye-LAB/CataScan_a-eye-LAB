package org.cataract.web.application.service;

import java.io.OutputStream;
import java.util.List;

public interface DataDownloadService {

    byte[] downloadImageDataByteArr(List<Integer> institutionIds);

}

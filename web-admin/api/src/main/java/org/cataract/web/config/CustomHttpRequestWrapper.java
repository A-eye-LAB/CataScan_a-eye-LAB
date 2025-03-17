package org.cataract.web.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;


import java.io.*;

public class CustomHttpRequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody;
    private boolean isMultipart;

    public CustomHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        // Check if the request is multipart
        this.isMultipart = request.getRequestURI().equals("/reports") && request.getMethod().contains("POST");

        if (!this.isMultipart) {
            try (InputStream is = request.getInputStream();
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                this.requestBody = byteArrayOutputStream.toByteArray();
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        if (isMultipart) {
            // If it's multipart, return the original request's input stream
            try {
                return super.getRequest().getInputStream();
            } catch (IOException e) {
                throw new RuntimeException("Failed to get input stream for multipart request", e);
            }
        }

        // For non-multipart requests, return the cached request body
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.requestBody);
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Not implemented
            }
        };
    }

    public byte[] getRequestBody() {
        return this.requestBody;
    }

    public boolean isMultipart() {
        return isMultipart;
    }
}

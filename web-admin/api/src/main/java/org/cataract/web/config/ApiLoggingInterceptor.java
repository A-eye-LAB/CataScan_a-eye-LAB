package org.cataract.web.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class ApiLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        // Generate unique request ID for tracing
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        response.setHeader("X-Request-ID", requestId); // Pass requestId in response

        logRequest(request, handler);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        logResponse(request, response, duration);
        MDC.clear();
    }

    private void logRequest(HttpServletRequest request, Object handler) throws IOException {
        if (request instanceof CustomHttpRequestWrapper) {
            CustomHttpRequestWrapper requestWrapper = (CustomHttpRequestWrapper) request;
            String requestBody = new String(requestWrapper.getRequestBody());
            if (!requestBody.isEmpty() && !isSensitiveData(request)) {
                log.info("Request Method: [{}] URL: [{}] Body: [{}]", request.getMethod(), request.getRequestURI(), requestBody);
            }
        }
        if (request.getParameterNames().hasMoreElements()) {
            log.info("Request Method: [{}] URL: [{}] Params: [{}]",request.getMethod(), request.getRequestURI(), getRequestParams(request));
        }
        else {
            log.info("Request Method: [{}] URL: [{}]",request.getMethod(), request.getRequestURI());
        }

/*        // PathVariable Logging
        if (handler instanceof HandlerMethod) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest currentRequest = attributes.getRequest();
                Map<String, String> pathVariables = (Map<String, String>) currentRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

                if (pathVariables != null) {
                    log.info("Request Method: [{}] URL: [{}] pathVariables: [{}]", request.getMethod(), request.getRequestURI(), pathVariables);
                }
            }
        }*/

    }

    private boolean isSensitiveData(HttpServletRequest request) {
        return request.getMethod().equals("POST") && (request.getRequestURI().contains("admin") || request.getRequestURI().contains("auth"));
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
        int status = response.getStatus();
        HttpStatus httpStatus = HttpStatus.resolve(status);
        String statusText = httpStatus != null ? httpStatus.getReasonPhrase() : "Unknown";

        if (response instanceof CustomHttpResponseWrapper responseWrapper) {
            byte[] responseData = responseWrapper.getResponseData();
            if (responseData != null && responseData.length > 0 && isSensitiveData(request)) {
                String responseBody = new String(responseData);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(responseBody, Object.class);
                    String prettyBody = mapper.writeValueAsString(json);
                    log.info("Response Status: [{}] URL: [{}] | Status: {} - {} | Duration: {}ms | Body: [{}] ", response.getStatus(),
                            request.getRequestURI(), status, statusText, duration, prettyBody);
                } catch (JsonProcessingException e) {
                    log.info("Failed to log response statusResponse Status: [{}] URL: [{}] | Status: {} - {} | " +
                                    "Duration: {}ms | Body: [{}] ", response.getStatus(),
                            request.getRequestURI(), status, statusText, duration, e.getMessage());
                }
            } else {
                log.info("Response Status: [{}] URL: [{}] | Status: {} - {} | Duration: {}ms | Body: [Empty]", response.getStatus(),
                        request.getRequestURI(), status, statusText, duration);
            }
        } else {
            log.info("Response Status: [{}] URL: [{}] | Status : {} - {}", response.getStatus(), request.getRequestURI(),
                    status, statusText);
        }
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }

        return paramMap;
    }


}
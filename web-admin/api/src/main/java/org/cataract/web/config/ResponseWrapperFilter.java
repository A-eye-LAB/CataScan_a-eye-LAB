package org.cataract.web.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResponseWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            CustomHttpResponseWrapper responseWrapper = new CustomHttpResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, responseWrapper);

            byte[] responseData = responseWrapper.getResponseData();
            if (responseData != null && responseData.length > 0) {
                response.getOutputStream().write(responseData);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}

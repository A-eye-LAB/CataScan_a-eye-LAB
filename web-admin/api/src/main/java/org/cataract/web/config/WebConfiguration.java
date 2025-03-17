package org.cataract.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${app.image.filepath}")
    String uploadDir;
    @Value("${app.image.baseurl}")
    String imageUrlPath;

    private final RateLimitingInterceptor rateLimitingInterceptor;
    private final ApiLoggingInterceptor apiLoggingInterceptor;
    public WebConfiguration(RateLimitingInterceptor rateLimitingInterceptor,
                            ApiLoggingInterceptor apiLoggingInterceptor) {
        this.rateLimitingInterceptor = rateLimitingInterceptor;
        this.apiLoggingInterceptor = apiLoggingInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler( imageUrlPath + "/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor).addPathPatterns("/auth/**");
        registry.addInterceptor(apiLoggingInterceptor).addPathPatterns("/**");
    }
}
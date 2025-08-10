package com.shepherd.shepslibrary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final TimezoneInterceptor timezoneInterceptor;

    public WebConfig(TimezoneInterceptor timezoneInterceptor) {
        this.timezoneInterceptor = timezoneInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timezoneInterceptor)
                .addPathPatterns("/**");
    }
}

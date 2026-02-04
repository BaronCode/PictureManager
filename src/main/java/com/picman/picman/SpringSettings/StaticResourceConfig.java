package com.picman.picman.SpringSettings;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/thumbs/**")
                .addResourceLocations("file:".concat(Settings.get("th_output")));
        registry.addResourceHandler("/images/*.*")
                .addResourceLocations("file:".concat(Settings.get("output")));
    }
}
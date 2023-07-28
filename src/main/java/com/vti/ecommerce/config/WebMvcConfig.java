package com.vti.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    public void addResourceHandlers(final ResourceHandlerRegistry registry){
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + "uploads/");
    }
}

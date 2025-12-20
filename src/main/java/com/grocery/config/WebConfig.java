package com.grocery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Get the Current Working Directory (Project Root)
        String projectRoot = System.getProperty("user.dir");
        String uploadDirName = "uploads/products";
        // 2. Combine the paths and convert to a file URI string
        // This handles spaces and ensures correct slashes for a URL
        Path absolutePath = Paths.get(projectRoot, uploadDirName);
        // Convert to a file URI string (e.g., file:/C:/...) and ensure trailing slash
        String fileResourcePath = absolutePath.toUri().toString();

        if (!fileResourcePath.endsWith("/")) {
            fileResourcePath += "/";
        }
        System.out.println("Final Serving Path Check (HTTP Mapping): " + fileResourcePath);
        // The browser requests: http://localhost:8080/images/filename.png
        // Spring maps it to: file:/C:/.../products/filename.png
        registry.addResourceHandler("/images/**")
                .addResourceLocations(fileResourcePath);
	}
}

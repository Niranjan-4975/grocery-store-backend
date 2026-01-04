package com.grocery.product.service;

import java.io.IOException;
import java.nio.file.*;

import com.grocery.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	
	public String saveFile(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new FileStorageException("Failed to store empty file.");
			}
			
			String fileName =  System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
			Path uploadPath = Paths.get(uploadDir);
			
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
			return "/images/" + fileName; //URL to be return to Frontend
			
			
		} catch (IOException e) {
			throw new FileStorageException("Could not store file. Error: "+ e.getMessage());
		}
	}

}

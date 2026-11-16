package com.grocery.product.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.grocery.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	
	@Autowired
	private Cloudinary cloudinary;

	@Value("${cloudinary.cloud-name:}")
	private String cloudName;

	@Value("${cloudinary.upload-preset:}")
	private String uploadPreset;

	@Value("${file.upload-dir:uploads/products}")
	private String uploadDir;

	public String saveFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new FileStorageException("Failed to store empty file.");
		}

		// If Cloudinary credentials are configured, upload to Cloudinary
		if (cloudName != null && !cloudName.trim().isEmpty() && !"demo".equalsIgnoreCase(cloudName.trim())) {
			try {
				System.out.println("Uploading product image to Cloudinary (cloud_name: " + cloudName + ")...");
				Map<String, Object> params = new java.util.HashMap<>();
				params.put("folder", "grocery_products");
				params.put("resource_type", "auto");
				if (uploadPreset != null && !uploadPreset.trim().isEmpty()) {
					params.put("upload_preset", uploadPreset.trim());
				}
				Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
				String secureUrl = (String) uploadResult.get("secure_url");
				System.out.println("Successfully uploaded image to Cloudinary: " + secureUrl);
				return secureUrl;
			} catch (Exception e) {
				System.err.println("Error uploading image to Cloudinary: " + e.getMessage());
				throw new FileStorageException("Failed to upload image to Cloudinary: " + e.getMessage());
			}
		}

		// Fallback local storage if Cloudinary credentials are not yet set
		try {
			System.out.println("Cloudinary not configured. Storing image locally...");
			String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
			Path uploadPath = Paths.get(uploadDir);
			
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
			return "/images/" + fileName;
		} catch (IOException e) {
			throw new FileStorageException("Could not store file locally. Error: " + e.getMessage());
		}
	}

	public void deleteFile(String imageUrl) {
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			return;
		}

		// If Cloudinary URL, delete from Cloudinary
		if (imageUrl.contains("cloudinary.com")) {
			try {
				String publicId = extractPublicId(imageUrl);
				if (publicId != null) {
					cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
				}
			} catch (Exception e) {
				System.err.println("Warning: Failed to delete image from Cloudinary: " + e.getMessage());
			}
		}
	}

	private String extractPublicId(String imageUrl) {
		try {
			int uploadIndex = imageUrl.indexOf("/upload/");
			if (uploadIndex == -1) return null;
			String path = imageUrl.substring(uploadIndex + 8);
			if (path.startsWith("v") && path.contains("/")) {
				path = path.substring(path.indexOf("/") + 1);
			}
			int lastDot = path.lastIndexOf(".");
			if (lastDot != -1) {
				path = path.substring(0, lastDot);
			}
			return path;
		} catch (Exception e) {
			return null;
		}
	}
}

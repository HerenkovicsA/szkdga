package com.szakdolgozat.service;

import org.springframework.web.multipart.MultipartFile;

public interface StoreFileService {
	
	String store(MultipartFile file, String filePath, String contextPath);

	String store(MultipartFile file, boolean overwrite,  String contextPath);
}

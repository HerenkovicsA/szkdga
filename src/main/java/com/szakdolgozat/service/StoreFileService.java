package com.szakdolgozat.service;

import org.springframework.web.multipart.MultipartFile;

public interface StoreFileService {
	
	String store(MultipartFile file);
}

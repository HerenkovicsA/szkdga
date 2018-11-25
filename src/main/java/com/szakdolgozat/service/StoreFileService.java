package com.szakdolgozat.service;

import org.springframework.web.multipart.MultipartFile;

public interface StoreFileService {
	
	String store(MultipartFile file, String string);

	String store(MultipartFile file, boolean overwrite);
}

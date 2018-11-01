package com.szakdolgozat.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StoreFileServiceImpl implements StoreFileService{

    private final String IMAGE_STORE_DIRECTORY = System.getProperty("image.storage", new File("images").getAbsolutePath());
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public String store(MultipartFile file) {
		File directory = new File(IMAGE_STORE_DIRECTORY);
		if(! directory.isDirectory()) {
			LOG.warn(directory + " doesnt exists.It will be created.");
			try{
				directory.mkdir();
			}catch(Exception e) {
				LOG.error(e.getMessage());
			}
		}
		LOG.info("Saving " + file.getOriginalFilename() + " to " + directory);
		String pathToFile = IMAGE_STORE_DIRECTORY + "\\" + file.getOriginalFilename();		
        try {
			file.transferTo(new File(pathToFile));
		} catch (IllegalStateException | IOException e) {
			LOG.error(e.getMessage());
			
		}
        return pathToFile;
    }
}

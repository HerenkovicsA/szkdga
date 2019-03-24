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

    private final String ENV = System.getProperty("environment");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
	public String store(MultipartFile file, boolean overwrite, String contextPath) {
		File directory = new File(contextPath + File.separator + "images");
		if(! directory.isDirectory()) {
			LOG.warn(directory + " doesnt exists. It will be created.");
			try{
				directory.mkdir();
			}catch(Exception e) {
				LOG.error(e.getMessage());
			}
		}
		String pathToFile = contextPath + File.separator + "images" + File.separator + file.getOriginalFilename();		
        try {
        	File fileToSave = new File(pathToFile);
        	while(fileToSave.exists() && !overwrite) {
        		int lastDot = pathToFile.lastIndexOf('.');
        		pathToFile = pathToFile.substring(0, lastDot) + "_" + (int)(Math.random()*10) + pathToFile.substring(lastDot);
        		fileToSave = new File(pathToFile);
        	}
        	LOG.info("Saving " + fileToSave.getName() + " to " + directory);
			file.transferTo(fileToSave);
			if(ENV != null && ENV.equals("test")) {
				return "\\images\\" + fileToSave.getName();
			}
		} catch (IllegalStateException | IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
        return pathToFile.substring(contextPath.length());
    }

	@Override
	public String store(MultipartFile file, String path, String contextPath) {
		File actual = new File(path);
		if(actual.getName().equals(file.getOriginalFilename())) {
			return store(file, true, contextPath);
		} else {
			actual.delete();
			return store(file, false, contextPath);
		}
	}
	
}

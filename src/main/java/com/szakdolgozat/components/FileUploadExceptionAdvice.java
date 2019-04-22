package com.szakdolgozat.components;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileUploadBase.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.ui.Model;

@ControllerAdvice
public class FileUploadExceptionAdvice {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
    @ExceptionHandler({MaxUploadSizeExceededException.class,SizeLimitExceededException.class})
    public String handleMaxSizeException(
      MaxUploadSizeExceededException exc, HttpServletRequest request, 
      HttpServletResponse responsem, Model model) throws IOException {
    	log.warn(exc.getLocalizedMessage());
    	model.addAttribute("message", "s2l");
    	return "redirect:/error";
    }
}

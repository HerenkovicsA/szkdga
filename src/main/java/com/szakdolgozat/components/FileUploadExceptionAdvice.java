package com.szakdolgozat.components;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class FileUploadExceptionAdvice {
      
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxSizeException(
      MaxUploadSizeExceededException exc, HttpServletRequest request, 
      HttpServletResponse response) throws IOException {
    	System.out.println(exc.getLocalizedMessage());
    	ModelAndView maV = new ModelAndView("redirect:/error");
    	maV.addObject("message", "s2l");
    	return maV;
    }
}

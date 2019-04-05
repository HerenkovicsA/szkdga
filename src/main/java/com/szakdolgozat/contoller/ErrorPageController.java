package com.szakdolgozat.contoller;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Controller
public class ErrorPageController implements ErrorController {
	 
	private static final String ERR_PATH = "/error";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private ErrorAttributes errorAttributes;
	
	@Autowired
	public void setErrorAttributes(ErrorAttributes errorAttributes){
		this.errorAttributes = errorAttributes;
	}
	
	@RequestMapping(ERR_PATH)
	public String error(Model model, HttpServletRequest request, @RequestParam(value = "message", required=false) String message) {
		WebRequest rA = new ServletWebRequest(request);
		Map<String,Object> error = this.errorAttributes.getErrorAttributes( rA, true);
		String link = "/";
		if(error.get("error").equals("None")) {
			if(message != null && message.equals("s2l")){
				model.addAttribute("error","A fájl mérete túl nagy");
				model.addAttribute("message", "Méretezze újra a fájlt vagy töltsön fel egy másikat");
				link += "admin/products?p";
			}
		} else {
			model.addAttribute("error",error.get("error"));
			model.addAttribute("message","Kérem keresse fel a rendszergazdát a hibával kapcsolatban.");
		}
		model.addAttribute("timestamp",LocalDateTime.now().format(formatter));
		model.addAttribute("link",link);
		
		return "detailedError";
	}
	
	@Override
	public String getErrorPath() {
		return ERR_PATH;
	}

}

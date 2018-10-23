package com.szakdolgozat.contoller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.szakdolgozat.domain.User;
import com.szakdolgozat.service.PostCodeService;
import com.szakdolgozat.service.UserService;

@Controller
public class RegistrationController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private UserService us;
	private PostCodeService pcs;	
	
	@Autowired
	public RegistrationController(UserService us, PostCodeService pcs) {
		this.us = us;
		this.pcs = pcs;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}

	@GetMapping("/registration")
	public String registration(Model model){
		model.addAttribute("user", new User());
		return "auth/registration";
	}
	
	@PostMapping("/registration")
    public String reg(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
		log.info("Uj user!");
		//if returns 0, email address is already in use
		if(bindingResult.hasErrors())
        {
			System.out.println("error with reg");
            return "auth/registration";
        }		
		if(!pcs.checkPostCodeAndCity(user.getPostCode(), user.getCity())) {
			log.debug("City: " + user.getCity() + " and postcode: " + user.getPostCode() + " is not equal");
			model.addAttribute("postCodeError", user.getCity() + " irányítószáma nem: " + user.getPostCode() + " !");
			return "auth/registration";
		}
		int result = us.registerUser(user);
		if(result == 0) {
			log.debug("reg problem: email already exists");
			model.addAttribute("emailError", user.getEmail() + " email cím már létezik");
			return "auth/registration";
		}
		else {
			return "auth/login";
		}
    }
}

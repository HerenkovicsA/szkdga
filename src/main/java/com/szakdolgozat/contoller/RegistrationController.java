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
import com.szakdolgozat.service.GoogleService;
import com.szakdolgozat.service.PostCodeService;
import com.szakdolgozat.service.UserService;

@Controller
public class RegistrationController {
	
	private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);
	
	private UserService us;
	private PostCodeService pcs;
	private GoogleService gs;
	
	@Autowired
	public RegistrationController(UserService us, PostCodeService pcs, GoogleService gs) {
		this.us = us;
		this.pcs = pcs;
		this.gs = gs;
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
		//if returns 0, email address is already in use
		if(bindingResult.hasErrors())
        {
			LOG.warn("There were errors during registration");
            return "auth/registration";
        }		
		if(!pcs.checkPostCodeAndCity(user.getPostCode(), user.getCity())) {
			LOG.debug("City: " + user.getCity() + " and postcode: " + user.getPostCode() + " is not equal");
			model.addAttribute("postCodeError", user.getCity() + " irányítószáma nem: " + user.getPostCode() + " !");
			return "auth/registration";
		}
		if(!gs.validateAddress(user.getFullAddress())) {
			LOG.warn("Address is probably not valid");
			model.addAttribute("invalidAddress", "A cím: " + user.getFullAddress() + " nem biztos, hogy létezik. Kérem ellenőrizze, hogy nem írt el"
					+ "valamit. (pl. 'ut'-at írt 'út' vagy 'utca' helyett)");
			return "auth/registration";
		}
		int result = us.registerUser(user);
		if(result == 0) {
			LOG.debug("reg problem: email already exists");
			model.addAttribute("emailError", user.getEmail() + " email cím már létezik");
			return "auth/registration";
		}
		else {
			return "auth/login";
		}
    }
}

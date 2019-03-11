package com.szakdolgozat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.domain.PostCodeToCity;
import com.szakdolgozat.repository.PostCodeToCityRepository;

@Service
public class PostCodeServiceImpl implements PostCodeService{

	private PostCodeToCityRepository pctcr;
	
	@Autowired
	public PostCodeServiceImpl(PostCodeToCityRepository pctcr) {
		this.pctcr = pctcr;
	}

	@Override
	public boolean checkPostCodeAndCity(int postCode, String city) {
		List<PostCodeToCity> cities = pctcr.findByPostCode(postCode);
		for (PostCodeToCity postCodeToCity : cities) {
			if(postCodeToCity.getCityName().toLowerCase().equals(city.toLowerCase())) return true;
		}
		return false;
	}
}

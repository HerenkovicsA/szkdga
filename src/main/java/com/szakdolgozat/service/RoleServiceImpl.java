package com.szakdolgozat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.domain.Role;
import com.szakdolgozat.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {

	private RoleRepository rr;
	
	@Autowired
	public RoleServiceImpl(RoleRepository rr) {
		super();
		this.rr = rr;
	}

	@Override
	public Role findRoleByName(String name) {
		return rr.findByName(name);
	}

}

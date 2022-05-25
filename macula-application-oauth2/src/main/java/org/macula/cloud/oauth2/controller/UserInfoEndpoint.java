package org.macula.cloud.oauth2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoEndpoint {

	@GetMapping(value = { "/user/me", "/api/user/me" })
	public Object user(Authentication authentication) {
		return authentication;
	}

}

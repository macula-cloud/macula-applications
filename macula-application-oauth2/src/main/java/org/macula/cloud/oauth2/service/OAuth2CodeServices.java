package org.macula.cloud.oauth2.service;

import org.macula.cloud.oauth2.domain.OAuth2Code;
import org.macula.cloud.oauth2.repository.OAuth2CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

@Service
public class OAuth2CodeServices extends RandomValueAuthorizationCodeServices {

	@Autowired
	private OAuth2CodeRepository oauth2CodeRepository;

	@Override
	protected void store(String code, OAuth2Authentication authentication) {
		OAuth2Code auth2Code = new OAuth2Code();
		auth2Code.setCode(code);
		auth2Code.setOauth2Authentication(authentication);
		oauth2CodeRepository.save(auth2Code);
	}

	@Override
	protected OAuth2Authentication remove(String code) {
		OAuth2Authentication authentication = null;
		OAuth2Code oauth2Code = oauth2CodeRepository.findByCode(code);
		if (oauth2Code != null) {
			authentication = oauth2Code.getOauth2Authentication();
			oauth2CodeRepository.deleteByCode(code);
		}
		return authentication;
	}

}

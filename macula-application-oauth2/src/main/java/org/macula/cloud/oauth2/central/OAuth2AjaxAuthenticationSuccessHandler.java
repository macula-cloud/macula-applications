package org.macula.cloud.oauth2.central;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.macula.cloud.security.authentication.AjaxAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OAuth2AjaxAuthenticationSuccessHandler implements AjaxAuthenticationSuccessHandler {

	private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private AuthorizationServerTokenServices authorizationServerTokenServices;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String clientId = request.getParameter(OAuth2Utils.CLIENT_ID);
		String grantType = "code";
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

		TokenRequest tokenRequest = new TokenRequest(new HashMap(), clientId, Sets.newHashSet(), grantType);
		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

		OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
		log.info("Create login token success: {}", oAuth2AccessToken.getValue());

		ExtendedModelMap model = new ExtendedModelMap();
		model.addAttribute(oAuth2AccessToken);

		try {
			new MappingJackson2JsonView(MAPPER).render(model.asMap(), request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

}

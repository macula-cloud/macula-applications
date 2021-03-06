package org.macula.cloud.gateway.oauth2;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;

@Slf4j
public class OAuth2AuthenticationManager implements ReactiveAuthenticationManager {

	private ResourceServerTokenServices tokenServices;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		Authentication result = null;
		if (authentication instanceof BearerTokenAuthenticationToken) {
			String token = ((BearerTokenAuthenticationToken) authentication).getToken();
			try {
				OAuth2Authentication auth = tokenServices.loadAuthentication(token);
				if (auth != null) {
					if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
						OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
						// Guard against a cached copy of the same details
						if (!details.equals(auth.getDetails())) {
							// Preserve the authentication details from the one loaded by token services
							details.setDecodedDetails(auth.getDetails());
						}
					}
					auth.setDetails(authentication.getDetails());
					auth.setAuthenticated(true);
					result = auth;
				}
			} catch (InvalidTokenException ex) {
				log.error("OAuth2AuthenticationManager  tokenServices.loadAuthentication error :  ", ex);
			}
		}
		return Mono.justOrEmpty(result);
	}

	public void setTokenServices(ResourceServerTokenServices tokenServices) {
		this.tokenServices = tokenServices;
	}

}

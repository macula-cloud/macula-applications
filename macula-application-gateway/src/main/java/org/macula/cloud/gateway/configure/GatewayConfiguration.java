package org.macula.cloud.gateway.configure;

import lombok.AllArgsConstructor;
import org.macula.cloud.core.configure.CoreConfigurationProperties;
import org.macula.cloud.core.oauth2.SubjectAuthoritiesExtractor;
import org.macula.cloud.core.oauth2.SubjectPrincipalExtractor;
import org.macula.cloud.core.oauth2.SubjectPrincipalUserInfoTokenServices;
import org.macula.cloud.gateway.filter.JWTAuthenticationSignFilter;
import org.macula.cloud.gateway.filter.RedirectResponseGlobalFilter;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@Configuration
@AllArgsConstructor
public class GatewayConfiguration {
	private OAuth2ProtectedResourceDetails client;

	private ResourceServerProperties resource;

	private CoreConfigurationProperties properties;

	@Bean
	public UserInfoTokenServices subjectPrincipalUserInfoTokenServices() {
		SubjectAuthoritiesExtractor authoritesExtractor = new SubjectAuthoritiesExtractor(properties.getSecurity().getDefaultRole());
		SubjectPrincipalUserInfoTokenServices tokenServices = new SubjectPrincipalUserInfoTokenServices(resource.getUserInfoUri(),
				resource.getClientId(), new SubjectPrincipalExtractor(authoritesExtractor), authoritesExtractor,
				new MacSigner(properties.getSecurity().getJwtKey()));
		tokenServices.setRestTemplate(new OAuth2RestTemplate(client));
		return tokenServices;
	}

	@Bean
	public JWTAuthenticationSignFilter jwtAuthenticationFilter(ServerSecurityContextRepository repository) {
		return new JWTAuthenticationSignFilter(repository, new MacSigner(properties.getSecurity().getJwtKey()));
	}

	@Bean
	public RedirectResponseGlobalFilter redirectResponseGlobalFilter() {
		return new RedirectResponseGlobalFilter();
	}

}

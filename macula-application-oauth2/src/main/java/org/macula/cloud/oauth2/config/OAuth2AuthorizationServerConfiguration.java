package org.macula.cloud.oauth2.config;

import org.macula.cloud.oauth2.client.OAuth2ClientDetailsService;
import org.macula.cloud.oauth2.service.OAuth2CodeServices;
import org.macula.cloud.oauth2.service.OAuth2TokenStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	private AuthenticationManager authenticationManager;
	private OAuth2ClientDetailsService clientDetailsService;
	private OAuth2TokenStore tokenStore;
	private OAuth2CodeServices authorizationCodeServices;
	private UserDetailsService userDetailsService;

	public OAuth2AuthorizationServerConfiguration(AuthenticationManager authenticationManager, OAuth2ClientDetailsService clientDetailsService,
			OAuth2CodeServices authorizationCodeServices, UserDetailsService userDetailsService, OAuth2TokenStore tokenStore) {
		this.authenticationManager = authenticationManager;
		this.clientDetailsService = clientDetailsService;
		this.authorizationCodeServices = authorizationCodeServices;
		this.userDetailsService = userDetailsService;
		this.tokenStore = tokenStore;
	}

	@Override
	public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.allowFormAuthenticationForClients().tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}

	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager).authorizationCodeServices(authorizationCodeServices)
				.userDetailsService(userDetailsService).tokenStore(tokenStore);
	}

}

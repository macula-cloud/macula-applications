package org.macula.cloud.oauth2.config;

import java.util.List;

import org.macula.cloud.oauth2.central.CentralAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class GlobalAuthenticationConfigurer extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	private List<CentralAuthenticationProvider> channelAuthenticationProviders;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		OrderComparator.sort(channelAuthenticationProviders);
		for (CentralAuthenticationProvider channelAuthenticationProvider : channelAuthenticationProviders) {
			auth.authenticationProvider(channelAuthenticationProvider);
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

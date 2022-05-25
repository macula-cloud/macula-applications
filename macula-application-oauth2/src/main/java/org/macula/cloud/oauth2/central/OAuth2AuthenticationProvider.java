package org.macula.cloud.oauth2.central;

import java.util.List;

import org.macula.cloud.core.oauth2.LoginCredential;
import org.macula.cloud.core.oauth2.SubjectPrincipal;
import org.macula.cloud.oauth2.channel.ChannelAuthentication;
import org.macula.cloud.oauth2.channel.ChannelAuthenticationDetails;
import org.macula.cloud.oauth2.exception.OAuth2AuthenticationException;
import org.macula.cloud.oauth2.source.SourceLoginStrategy;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.OrderComparator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements CentralAuthenticationProvider {

	private final List<ChannelAuthentication> channelAuthentications;
	private final List<SourceLoginStrategy> sourceLoginStrategies;
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	public OAuth2AuthenticationProvider(List<ChannelAuthentication> channelAuthentications, List<SourceLoginStrategy> sourceLoginStrategies) {
		this.channelAuthentications = channelAuthentications;
		this.sourceLoginStrategies = sourceLoginStrategies;
		OrderComparator.sort(channelAuthentications);
		OrderComparator.sort(sourceLoginStrategies);
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		LoginCredential credential = createLoginCredential(authentication);
		if (credential.getUsername() == null) {
			credential.setUsername(authentication.getName());
		}
		return loginAuthentication(credential);
	}

	protected UserDetails loginAuthentication(LoginCredential credential) throws AuthenticationException {
		for (ChannelAuthentication channelAuthentication : channelAuthentications) {
			if (channelAuthentication.support(credential)) {
				SubjectPrincipal principal = channelAuthentication.loginAuthentication(credential);
				if (principal != null) {
					principal.setCurrentCredential(credential);
					for (SourceLoginStrategy loginStrategy : sourceLoginStrategies) {
						if (loginStrategy.support(principal.getSource()) && loginStrategy.validate(principal, credential)) {
							return principal;
						}
					}
				}
			}
		}
		throw new OAuth2AuthenticationException("usernameNotFoundOrPasswordIsWrong");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE + 100;
	}

	protected LoginCredential createLoginCredential(Authentication authentication) {
		Object details = authentication.getDetails();
		if (details instanceof ChannelAuthenticationDetails) {
			LoginCredential credential = ((ChannelAuthenticationDetails) details).getCredential();
			//	credential.setPassword(LoginPasswordDecoder.decode(authentication.getCredentials().toString()));
			credential.setPassword(authentication.getCredentials().toString());
			return credential;
		}
		LoginCredential credential = new LoginCredential();
		credential.setUsername(authentication.getName());
		if (authentication.getCredentials() instanceof String) {
			credential.setPassword((String) authentication.getCredentials());
		}
		return credential;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

	}

}

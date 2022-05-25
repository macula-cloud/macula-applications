package org.macula.cloud.oauth2.source;

import org.macula.cloud.core.oauth2.LoginCredential;
import org.macula.cloud.core.oauth2.SubjectPrincipal;
import org.macula.cloud.oauth2.repository.UserSocialRepository;
import org.springframework.stereotype.Component;

@Component
public class UserSocialLoginStrategy implements SourceLoginStrategy {

	private UserSocialRepository userSocialRepository;

	public UserSocialLoginStrategy(UserSocialRepository userSocialRepository) {
		this.userSocialRepository = userSocialRepository;
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

	@Override
	public boolean support(String source) {
		return "SOCIAL".equalsIgnoreCase(source);
	}

	@Override
	public boolean validate(SubjectPrincipal principal, LoginCredential credential) {
		String username = principal.getUsername();
		String openId = credential.getPassword();
		return userSocialRepository.findSocialUser(username, openId) != null;
	}

}

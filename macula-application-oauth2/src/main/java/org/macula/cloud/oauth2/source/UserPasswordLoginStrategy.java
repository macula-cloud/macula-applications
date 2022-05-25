package org.macula.cloud.oauth2.source;

import org.macula.cloud.core.oauth2.LoginCredential;
import org.macula.cloud.core.oauth2.SubjectPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class UserPasswordLoginStrategy implements SourceLoginStrategy {

	private final PasswordEncoder passwordEncoder;

	public UserPasswordLoginStrategy(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

	@Override
	public boolean support(String source) {
		return ObjectUtils.isEmpty(source) || "NEW".equalsIgnoreCase(source);
	}

	@Override
	public boolean validate(SubjectPrincipal principal, LoginCredential credential) {
		return passwordEncoder.matches(credential.getPassword(), principal.getPassword());
	}

}

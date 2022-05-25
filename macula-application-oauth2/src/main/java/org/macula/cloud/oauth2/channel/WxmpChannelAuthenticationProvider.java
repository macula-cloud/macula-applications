package org.macula.cloud.oauth2.channel;

import org.macula.cloud.core.oauth2.LoginCredential;
import org.macula.cloud.core.oauth2.SubjectPrincipal;
import org.springframework.security.core.AuthenticationException;

public class WxmpChannelAuthenticationProvider implements ChannelAuthentication {

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean support(LoginCredential credential) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SubjectPrincipal loginAuthentication(LoginCredential credential) throws AuthenticationException {
		// TODO Auto-generated method stub
		return null;
	}

}

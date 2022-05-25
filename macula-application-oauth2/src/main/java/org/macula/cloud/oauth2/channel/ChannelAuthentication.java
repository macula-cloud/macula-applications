package org.macula.cloud.oauth2.channel;

import org.macula.cloud.core.oauth2.LoginCredential;
import org.macula.cloud.core.oauth2.SubjectPrincipal;
import org.springframework.core.Ordered;
import org.springframework.security.core.AuthenticationException;

public interface ChannelAuthentication extends Ordered {

	boolean support(LoginCredential credential);

	SubjectPrincipal loginAuthentication(LoginCredential credential) throws AuthenticationException;
}

package org.macula.cloud.oauth2.central;

import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;

public interface CentralAuthenticationProvider extends AuthenticationProvider, Ordered {

}

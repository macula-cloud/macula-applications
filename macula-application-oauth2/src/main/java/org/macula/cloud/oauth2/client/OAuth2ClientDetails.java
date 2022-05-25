package org.macula.cloud.oauth2.client;

import org.macula.cloud.core.domain.Application;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2ClientDetails extends BaseClientDetails {

	private static final long serialVersionUID = Version.version;

	private Application application;
}

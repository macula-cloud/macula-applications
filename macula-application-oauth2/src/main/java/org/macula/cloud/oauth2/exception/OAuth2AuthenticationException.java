package org.macula.cloud.oauth2.exception;

import org.macula.cloud.tools.exception.MaculaException;

public class OAuth2AuthenticationException extends MaculaException {

	private static final long serialVersionUID = Version.version;

	public OAuth2AuthenticationException(String message) {
		super(message);
	}

	@Override
	public String getParentCode() {
		return "MACULA-CLOUD-OAUTH2";
	}

}

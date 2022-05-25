package org.macula.cloud.oauth2.channel;

import org.apache.commons.lang3.StringUtils;
import org.macula.cloud.cache.utils.J2CacheUtils;
import org.macula.cloud.core.oauth2.LoginCredential;
import org.macula.cloud.core.oauth2.SubjectPrincipal;
import org.macula.cloud.oauth2.central.OAuth2UserDetailsService;
import org.macula.cloud.oauth2.exception.OAuth2AuthenticationException;
import org.macula.cloud.security.authentication.CaptchaValidationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class MobileChannelAuthenticationProvider implements ChannelAuthentication {

	private final OAuth2UserDetailsService userService;
	private final CaptchaValidationPolicy captchaVlidationPolicy;

	private MobileChannelAuthenticationProvider(OAuth2UserDetailsService userService, CaptchaValidationPolicy captchaVlidationPolicy) {
		this.userService = userService;
		this.captchaVlidationPolicy = captchaVlidationPolicy;
	}

	@Override
	public boolean support(LoginCredential credential) {
		return credential != null && credential.getMobile() != null && credential.getSms() != null;
	}

	@Override
	public SubjectPrincipal loginAuthentication(LoginCredential credential) throws AuthenticationException {
		if (captchaVlidationPolicy.isNeedCaptchaValidate(credential.getMobile())) {
			String captchaCode = credential.getCaptchaCode();
			String captcha = credential.getCaptcha();
			if (StringUtils.isEmpty(captcha)) {
				throw new OAuth2AuthenticationException("captchaNull");
			}
			if (!captcha.equalsIgnoreCase(captchaCode)) {
				throw new OAuth2AuthenticationException("captchaWrong");
			}
		}

		SubjectPrincipal subjectPrincipal = (SubjectPrincipal) userService.loadUserByUsername(credential.getMobile());
		if (subjectPrincipal == null) {
			throw new OAuth2AuthenticationException("mobileNotFoundWrong");
		}

		// login by sms
		String cachedSms = J2CacheUtils.get("sms", subjectPrincipal.getMobile());
		if (cachedSms != null && StringUtils.equals(cachedSms, credential.getSms())) {
			throw new OAuth2AuthenticationException("usernameNotFoundOrPasswordIsWrong");
		}

		return subjectPrincipal;
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

}

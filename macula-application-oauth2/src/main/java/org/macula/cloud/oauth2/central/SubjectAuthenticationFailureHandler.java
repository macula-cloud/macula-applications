package org.macula.cloud.oauth2.central;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.macula.cloud.core.configure.CoreConfigurationProperties;
import org.macula.cloud.core.utils.StringUtils;
import org.macula.cloud.security.authentication.CaptchaValidationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class SubjectAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private String loginPath;

	private CaptchaValidationPolicy captchaValidationPolicy;

	public SubjectAuthenticationFailureHandler(CoreConfigurationProperties properties, CaptchaValidationPolicy captchaValidationPolicy) {
		this.loginPath = properties.getSecurity().getLoginPath();
		this.captchaValidationPolicy = captchaValidationPolicy;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		String username = request.getParameter("username");
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.setAttribute("username", username);
			session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception.getMessage());
		}
		if (StringUtils.isNotEmpty(username)) {
			captchaValidationPolicy.increaseCaptchaMarks(username);
		}
		RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
		redirectStrategy.sendRedirect(request, response, loginPath + "?username=" + username);
	}
}

package org.macula.cloud.oauth2.central;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.macula.cloud.core.configure.CoreConfigurationProperties;
import org.macula.cloud.core.servlet.SessionIdPayload;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SubjectLogoutSuccessHandler implements LogoutSuccessHandler {

	private final TokenStore tokenStore;
	private final ServletContext servletContext;
	private final String loginPath;

	public SubjectLogoutSuccessHandler(CoreConfigurationProperties properties, TokenStore tokenStore, ServletContext servletContext) {
		this.tokenStore = tokenStore;
		this.servletContext = servletContext;
		this.loginPath = properties.getSecurity().getLoginPath();
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		Optional<String> token = SessionIdPayload.extract(request);
		if (token.isPresent()) {
			tokenStore.removeAccessToken(new DefaultOAuth2AccessToken(token.get()));
			tokenStore.removeRefreshToken(new DefaultOAuth2RefreshToken(token.get()));
		}
		;
		request.getSession().invalidate();

		String redirectPath = request.getParameter("redirect");
		if (redirectPath != null) {
			response.sendRedirect(redirectPath);
			return;
		}

		String referer = request.getHeader("Referer");
		if (referer != null) {
			response.sendRedirect(referer);
			return;
		}
		response.sendRedirect(servletContext.getContextPath() + loginPath);
	}
}

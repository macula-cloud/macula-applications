package org.macula.cloud.oauth2.channel;

import javax.servlet.http.HttpServletRequest;

import org.macula.cloud.core.principal.LoginCredential;
import org.macula.cloud.core.utils.HttpRequestUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class ChannelAuthenticationDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = 6975601077710753878L;

	private LoginCredential credential = new LoginCredential();

	public ChannelAuthenticationDetails(HttpServletRequest request) {
		super(request);

		credential.setClientId(request.getParameter("client_id"));
		credential.setUsername(request.getParameter("username"));
		credential.setPassword(request.getParameter("password"));
		credential.setMobile(request.getParameter("mobile"));
		credential.setSmsCode(request.getParameter("smsCode"));
		credential.setCaptchaId(request.getParameter("captchaId"));
		credential.setCaptchaCode((String) request.getSession().getAttribute("captchaCode"));
		credential.setRedirectUrl(HttpRequestUtils.getRedirectUrl(request));
		credential.setSignKey(request.getParameter("signKey"));
		credential.setSignCode(request.getParameter("signCode"));
		credential.setSessionId(this.getSessionId());
		credential.setRemoteAddress(this.getRemoteAddress());

		request.getSession().removeAttribute("captchaCode");
	}

	/**
	 * @return the credential
	 */
	public LoginCredential getCredential() {
		return credential;
	}

}

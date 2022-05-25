package org.macula.cloud.oauth2.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.macula.cloud.core.utils.SystemUtils;
import org.macula.cloud.oauth2.domain.OAuth2AccessToken;
import org.macula.cloud.oauth2.repository.OAuth2AccessTokenRepository;
import org.macula.cloud.oauth2.repository.OAuth2RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OAuth2TokenStore implements TokenStore {

	@Autowired
	private OAuth2AccessTokenRepository accessTokenRepository;
	@Autowired
	private OAuth2RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private AuthenticationKeyGenerator authenticationKeyGenerator;

	@Override
	public OAuth2Authentication readAuthentication(org.springframework.security.oauth2.common.OAuth2AccessToken token) {
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(String token) {
		OAuth2Authentication authentication = null;
		try {
			org.macula.cloud.oauth2.domain.OAuth2AccessToken oauth2AccessToken = accessTokenRepository.findByTokenId(extractTokenKey(token));
			if (oauth2AccessToken == null) {
				if (log.isInfoEnabled()) {
					log.info("Failed to find access token for token " + token);
				}
			} else {
				authentication = oauth2AccessToken.getOauth2Authentication();
			}
		} catch (IllegalArgumentException e) {
			log.warn("Failed to deserialize authentication for " + token, e);
			removeAccessToken(token);
		}
		return authentication;
	}

	@Override
	public void storeAccessToken(org.springframework.security.oauth2.common.OAuth2AccessToken token,
			org.springframework.security.oauth2.provider.OAuth2Authentication authentication) {
		Map<String, Object> additionalInfo = new HashMap<>();
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String sessionId = "";
		if (attr != null) {
			sessionId = attr.getRequest().getSession(true).getId();
		}
		additionalInfo.put("createTime", SystemUtils.getCurrentTime());
		additionalInfo.put("sessionId", sessionId);
		((DefaultOAuth2AccessToken) token).setAdditionalInformation(additionalInfo);

		String refreshToken = null;
		if (token.getRefreshToken() != null) {
			refreshToken = token.getRefreshToken().getValue();
		}

		if (readAccessToken(token.getValue()) != null) {
			removeAccessToken(token.getValue());
		}
		org.macula.cloud.oauth2.domain.OAuth2AccessToken accessToken = new org.macula.cloud.oauth2.domain.OAuth2AccessToken();
		accessToken.setTokenId(extractTokenKey(token.getValue()));
		accessToken.setOauth2Token(token);
		accessToken.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
		if (!authentication.isClientOnly()) {
			accessToken.setUserName(authentication.getName());
		}
		accessToken.setClientId(authentication.getOAuth2Request().getClientId());
		accessToken.setOauth2Authentication(authentication);
		accessToken.setRefreshToken(extractTokenKey(refreshToken));

		accessTokenRepository.save(accessToken);
	}

	@Override
	public org.springframework.security.oauth2.common.OAuth2AccessToken readAccessToken(String tokenValue) {
		org.springframework.security.oauth2.common.OAuth2AccessToken accessToken = null;

		try {
			OAuth2AccessToken oauth2Token = accessTokenRepository.findByTokenId(extractTokenKey(tokenValue));
			if (oauth2Token == null) {
				if (log.isInfoEnabled()) {
					log.info("Failed to find access token for token " + tokenValue);
				}
			} else {
				accessToken = oauth2Token.getOauth2Token();
			}
		} catch (IllegalArgumentException e) {
			log.warn("Failed to deserialize access token for " + tokenValue, e);
			removeAccessToken(tokenValue);
		}
		return accessToken;
	}

	@Override
	public void removeAccessToken(org.springframework.security.oauth2.common.OAuth2AccessToken token) {
		removeAccessToken(token.getValue());
	}

	public void removeAccessToken(String tokenValue) {
		accessTokenRepository.deleteByTokenId(extractTokenKey(tokenValue));
	}

	@Override
	public void storeRefreshToken(org.springframework.security.oauth2.common.OAuth2RefreshToken refreshToken,
			org.springframework.security.oauth2.provider.OAuth2Authentication authentication) {
		org.macula.cloud.oauth2.domain.OAuth2RefreshToken token = new org.macula.cloud.oauth2.domain.OAuth2RefreshToken();
		token.setTokenId(extractTokenKey(refreshToken.getValue()));
		token.setOauth2Authentication(authentication);
		token.setOauth2Token(refreshToken);
		refreshTokenRepository.save(token);
	}

	@Override
	public org.springframework.security.oauth2.common.OAuth2RefreshToken readRefreshToken(String tokenValue) {
		org.springframework.security.oauth2.common.OAuth2RefreshToken refreshToken = null;

		try {
			org.macula.cloud.oauth2.domain.OAuth2RefreshToken token = refreshTokenRepository.findByTokenId(extractTokenKey(tokenValue));
			if (token == null) {
				if (log.isInfoEnabled()) {
					log.info("Failed to find refresh token for token " + tokenValue);
				}
			} else {
				refreshToken = token.getOauth2Token();
			}
		} catch (IllegalArgumentException e) {
			log.warn("Failed to deserialize refresh token for token " + tokenValue, e);
			removeRefreshToken(tokenValue);
		}
		return refreshToken;
	}

	public void removeRefreshToken(String token) {
		refreshTokenRepository.deleteByTokenId(extractTokenKey(token));
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(org.springframework.security.oauth2.common.OAuth2RefreshToken token) {
		return readAuthenticationForRefreshToken(token.getValue());
	}

	@Override
	public void removeRefreshToken(org.springframework.security.oauth2.common.OAuth2RefreshToken token) {
		removeRefreshToken(token.getValue());
	}

	public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
		OAuth2Authentication authentication = null;
		try {
			org.macula.cloud.oauth2.domain.OAuth2RefreshToken token = refreshTokenRepository.findByTokenId(extractTokenKey(value));
			if (token == null) {
				if (log.isInfoEnabled()) {
					log.info("Failed to find access token for token " + value);
				}
			} else {
				authentication = token.getOauth2Authentication();
			}
		} catch (IllegalArgumentException e) {
			log.warn("Failed to deserialize access token for " + value, e);
			removeRefreshToken(value);
		}
		return authentication;
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(org.springframework.security.oauth2.common.OAuth2RefreshToken refreshToken) {
		removeAccessTokenUsingRefreshToken(refreshToken.getValue());
	}

	public void removeAccessTokenUsingRefreshToken(String refreshToken) {
		accessTokenRepository.deleteByRefreshToken(extractTokenKey(refreshToken));
	}

	@Override
	public org.springframework.security.oauth2.common.OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		org.springframework.security.oauth2.common.OAuth2AccessToken accessToken = null;

		String key = authenticationKeyGenerator.extractKey(authentication);
		try {
			OAuth2AccessToken token = accessTokenRepository.findByAuthenticationId(key);
			if (token == null) {
				if (log.isDebugEnabled()) {
					log.debug("Failed to find access token for authentication " + authentication);
				}
			} else {
				accessToken = token.getOauth2Token();
			}
		} catch (IllegalArgumentException e) {
			log.error("Could not extract access token for authentication " + authentication, e);
		}

		if (accessToken != null && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
			removeAccessToken(accessToken.getValue());
			// Keep the store consistent (maybe the same user is represented by this
			// authentication but the details have
			// changed)
			storeAccessToken(accessToken, authentication);
		}
		return accessToken;
	}

	@Override
	public Collection<org.springframework.security.oauth2.common.OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId,
			String username) {
		final List<org.springframework.security.oauth2.common.OAuth2AccessToken> accessTokens = new ArrayList<org.springframework.security.oauth2.common.OAuth2AccessToken>();

		List<OAuth2AccessToken> tokens = accessTokenRepository.findByUsernameAndClientId(username, clientId);
		if (CollectionUtils.isEmpty(tokens)) {
			if (log.isInfoEnabled()) {
				log.info("Failed to find access token for clientId " + clientId + " and userName " + username);
			}
		} else {
			tokens.forEach(t -> accessTokens.add(t.getOauth2Token()));
		}

		return removeNulls(accessTokens);
	}

	@Override
	public Collection<org.springframework.security.oauth2.common.OAuth2AccessToken> findTokensByClientId(String clientId) {
		final List<org.springframework.security.oauth2.common.OAuth2AccessToken> accessTokens = new ArrayList<org.springframework.security.oauth2.common.OAuth2AccessToken>();

		List<OAuth2AccessToken> tokens = accessTokenRepository.findByClientId(clientId);
		if (CollectionUtils.isEmpty(tokens)) {
			if (log.isInfoEnabled()) {
				log.info("Failed to find access token for clientId " + clientId);
			}
		} else {
			tokens.forEach(t -> accessTokens.add(t.getOauth2Token()));
		}

		return removeNulls(accessTokens);
	}

	private List<org.springframework.security.oauth2.common.OAuth2AccessToken> removeNulls(
			List<org.springframework.security.oauth2.common.OAuth2AccessToken> accessTokens) {
		List<org.springframework.security.oauth2.common.OAuth2AccessToken> tokens = new ArrayList<org.springframework.security.oauth2.common.OAuth2AccessToken>();
		for (org.springframework.security.oauth2.common.OAuth2AccessToken token : accessTokens) {
			if (token != null) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	protected String extractTokenKey(String value) {
		return value;
		//		if (value == null) {
		//			return null;
		//		}
		//		MessageDigest digest;
		//		try {
		//			digest = MessageDigest.getInstance("MD5");
		//		} catch (NoSuchAlgorithmException e) {
		//			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		//		}
		//
		//		try {
		//			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
		//			return String.format("%032x", new BigInteger(1, bytes));
		//		} catch (UnsupportedEncodingException e) {
		//			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		//		}
	}
}

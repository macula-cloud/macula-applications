package org.macula.cloud.oauth2.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.macula.cloud.oauth2.domain.OAuth2AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2AccessTokenRepository extends JpaRepository<OAuth2AccessToken, Long> {

	OAuth2AccessToken findByTokenId(String accessToken);

	@Transactional
	int deleteByTokenId(String accessToken);

	@Transactional
	int deleteByRefreshToken(String refreshToken);

	OAuth2AccessToken findByAuthenticationId(String authenticationId);

	List<OAuth2AccessToken> findByUsernameAndClientId(String userName, String clientId);

	List<OAuth2AccessToken> findByClientId(String clientId);

}

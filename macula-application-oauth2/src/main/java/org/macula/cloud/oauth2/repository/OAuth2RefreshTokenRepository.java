package org.macula.cloud.oauth2.repository;

import javax.transaction.Transactional;

import org.macula.cloud.oauth2.domain.OAuth2RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2RefreshTokenRepository extends JpaRepository<OAuth2RefreshToken, Long> {

	OAuth2RefreshToken findByTokenId(String refreshToken);

	@Transactional
	int deleteByTokenId(String refreshToken);

}

package org.macula.cloud.oauth2.repository;

import javax.transaction.Transactional;

import org.macula.cloud.oauth2.domain.OAuth2Code;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2CodeRepository extends JpaRepository<OAuth2Code, Long> {

	OAuth2Code findByCode(String code);

	@Transactional
	int deleteByCode(String code);

}

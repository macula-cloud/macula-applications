package org.macula.cloud.oauth2.repository;

import org.macula.cloud.oauth2.domain.UserSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserSocialRepository extends JpaRepository<UserSocial, Long> {

	@Query("from UserSocial s where s.username = ?1 and s.openId = ?2")
	UserSocial findSocialUser(String username, String openId);

}

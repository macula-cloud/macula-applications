package org.macula.cloud.oauth2.central;

import java.util.List;

import org.macula.cloud.core.oauth2.SubjectPrincipal;
import org.macula.cloud.core.utils.SystemUtils;
import org.macula.cloud.oauth2.domain.OAuth2User;
import org.macula.cloud.oauth2.repository.OAuth2UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserDetailsService implements UserDetailsService {

	private final OAuth2UserRepository oauth2UserRepository;

	public OAuth2UserDetailsService(OAuth2UserRepository oauth2UserRepository) {
		this.oauth2UserRepository = oauth2UserRepository;
	}

	@Override
	public SubjectPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
		OAuth2User user = oauth2UserRepository.findByLogin(username.toLowerCase());

		if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
			return null;
		}

		boolean accountNonExpired = (user.getInactiveDate() == null || user.getInactiveDate().after(SystemUtils.getCurrentTime()))
				&& (user.getEffectiveDate() == null || user.getEffectiveDate().before(SystemUtils.getCurrentTime()));

		// TODO need load authorities ???
		List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("macula-cloud-gateway");

		SubjectPrincipal principal = new SubjectPrincipal(user.getUsername(), user.getPassword(), !Boolean.FALSE.equals(user.getEnabled()),
				accountNonExpired, true, Boolean.FALSE.equals(user.getLocked()), authorityList);
		principal.setSource(user.getSource());
		principal.setAvatar(user.getAvatar());
		principal.setEmail(user.getEmail());
		principal.setLanguage(user.getLocale());
		principal.setMobile(user.getMobile());
		principal.setNickname(user.getNickname());
		principal.setOrganizationId(user.getOrgCode());
		principal.setTimeZone(user.getTimezone());
		principal.setTheme(user.getTheme());
		principal.setUserId(user.getId().toString());
		return principal;
	}

}

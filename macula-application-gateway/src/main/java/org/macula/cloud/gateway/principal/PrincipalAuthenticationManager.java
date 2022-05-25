package org.macula.cloud.gateway.principal;

import reactor.core.publisher.Mono;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.security.authentication.ReactiveAuthenticationManager;

public class PrincipalAuthenticationManager implements ReactiveAuthenticationManager {

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		return Mono.justOrEmpty((authentication != null && authentication.isAuthenticated()) ? authentication : null);
	}

}

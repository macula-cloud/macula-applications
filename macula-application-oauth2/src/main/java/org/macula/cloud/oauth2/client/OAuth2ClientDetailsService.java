package org.macula.cloud.oauth2.client;

import java.util.Map;

import org.macula.cloud.oauth2.domain.OAuth2Client;
import org.macula.cloud.oauth2.repository.OAuth2ClientRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAuth2ClientDetailsService implements ClientDetailsService {

	private OAuth2ClientRepository clientRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	public OAuth2ClientDetailsService(OAuth2ClientRepository clientRepository,
			ObjectProvider<ObjectMapper> objectMapperProvider) {
		this.clientRepository = clientRepository;
		objectMapperProvider.ifAvailable(provider -> {
			objectMapper = provider;
		});
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		OAuth2Client client = clientRepository.findByClientId(clientId);
		if (client == null) {
			throw new NoSuchClientException("No client found : " + clientId);
		}
		OAuth2ClientDetails clientDetails = new OAuth2ClientDetails();
		clientDetails.setAuthorizedGrantTypes(StringUtils.commaDelimitedListToSet(client.getSupportedGrantTypes()));
		clientDetails.setClientId(client.getClientId());
		clientDetails.setClientSecret(client.getClientSecret());
		clientDetails.setScope(StringUtils.commaDelimitedListToSet(client.getScope()));
		clientDetails.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(client.getAuthorities()));
		clientDetails.setResourceIds(StringUtils.commaDelimitedListToSet(client.getResourceIds()));
		clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
		clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
		clientDetails.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(client.getAutoApproveScopes()));
		clientDetails.setRegisteredRedirectUri(StringUtils.commaDelimitedListToSet(client.getLoginRedirectUri()));
		clientDetails.setApplication(client);
		String json = client.getAdditionalInformation();
		if (json != null) {
			try {
				Map<String, Object> additionalInformation = objectMapper.readValue(json,
						new TypeReference<Map<String, Object>>() {
						});
				clientDetails.setAdditionalInformation(additionalInformation);
			} catch (Exception e) {
				log.warn("Parse client {} addition information to JSON error: {}", clientId, e);
			}
		}
		return clientDetails;
	}

}

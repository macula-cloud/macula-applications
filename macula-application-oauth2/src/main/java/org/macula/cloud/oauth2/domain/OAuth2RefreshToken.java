package org.macula.cloud.oauth2.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.macula.cloud.core.domain.AbstractAuditable;
import org.springframework.security.oauth2.common.util.SerializationUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "MC_OAUTH2_REFRESH_TOKEN")
public class OAuth2RefreshToken extends AbstractAuditable<Long> implements Serializable {

	private static final long serialVersionUID = Version.version;

	@Column(name = "	TOKEN_ID", length = 128)
	private String tokenId;

	@JsonIgnore
	@Column(name = "AUTHENTICATION")
	@Lob
	private byte[] authentication;

	@JsonIgnore
	@Column(name = "TOKEN")
	@Lob
	private byte[] token;

	@Transient
	private org.springframework.security.oauth2.common.OAuth2RefreshToken oauth2Token;

	@Transient
	private org.springframework.security.oauth2.provider.OAuth2Authentication oauth2Authentication;

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String refreshToken) {
		this.tokenId = refreshToken;
	}

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public org.springframework.security.oauth2.common.OAuth2RefreshToken getOauth2Token() {
		if (oauth2Token == null && token != null) {
			oauth2Token = SerializationUtils.deserialize(token);
		}
		return oauth2Token;
	}

	public void setOauth2Token(org.springframework.security.oauth2.common.OAuth2RefreshToken oauth2Token) {
		this.oauth2Token = oauth2Token;
		this.token = SerializationUtils.serialize(oauth2Token);
	}

	public org.springframework.security.oauth2.provider.OAuth2Authentication getOauth2Authentication() {
		if (oauth2Authentication == null && authentication != null) {
			oauth2Authentication = SerializationUtils.deserialize(authentication);
		}
		return oauth2Authentication;
	}

	public void setOauth2Authentication(org.springframework.security.oauth2.provider.OAuth2Authentication oauth2Authentication) {
		this.oauth2Authentication = oauth2Authentication;
		this.authentication = SerializationUtils.serialize(oauth2Authentication);
	}

	public String getAccessToken() {
		return tokenId;
	}

	public void setAccessToken(String accessToken) {
		this.tokenId = accessToken;
	}
}

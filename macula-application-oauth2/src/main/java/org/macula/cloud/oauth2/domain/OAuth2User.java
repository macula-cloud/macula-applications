package org.macula.cloud.oauth2.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "MC_USER")
public class OAuth2User extends org.macula.cloud.core.domain.UserDetails implements Serializable {

	private static final long serialVersionUID = Version.version;

}

package org.macula.cloud.oauth2.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.macula.cloud.core.domain.Application;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "MC_OAUTH2_CLIENT")
public class OAuth2Client extends Application {

	private static final long serialVersionUID = Version.version;

}

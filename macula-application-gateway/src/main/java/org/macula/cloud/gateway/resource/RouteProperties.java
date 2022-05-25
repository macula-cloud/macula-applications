package org.macula.cloud.gateway.resource;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 路由配置类
 */
@Data
@RefreshScope
@ConfigurationProperties("macula.document")
public class RouteProperties {

	private final List<RouteResource> resources = new ArrayList<>();

}

package org.macula.cloud.gateway.event;

import org.macula.cloud.core.event.MaculaEvent;

public class GatewayRouteChangeEvent extends MaculaEvent<GatewayRoute> {

	private static final long serialVersionUID = Version.version;

	public GatewayRouteChangeEvent(GatewayRoute source) {
		super(source);
	}

}

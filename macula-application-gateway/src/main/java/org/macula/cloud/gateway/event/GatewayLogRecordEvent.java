package org.macula.cloud.gateway.event;

import org.macula.cloud.core.event.MaculaEvent;
import org.macula.engine.commons.domain.GatewayLogRecord;

public class GatewayLogRecordEvent extends MaculaEvent<GatewayLogRecord> {

	private static final long serialVersionUID = Version.version;

	public GatewayLogRecordEvent(GatewayLogRecord source) {
		super(source);
	}

}

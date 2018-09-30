package org.foxconn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseLog implements LogInterface{
	@Override
	public void logError(String msg) {
		getLogger().info(msg);
	}

	@Override
	public void logInfo(String msg) {
		getLogger().error(msg);
		
	}
	@Override
	public void logDebug(String msg) {
		getLogger().debug(msg);
		
	}
	public abstract Logger getLogger();

}

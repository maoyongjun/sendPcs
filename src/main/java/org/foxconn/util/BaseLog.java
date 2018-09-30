package org.foxconn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseLog implements LogInterface{
	private Logger logger =LoggerFactory.getLogger(BaseLog.class);
	@Override
	public void logError(String msg) {
		getLogger().info(msg);
	}

	@Override
	public void logInfo(String msg) {
		getLogger().error(msg);
		
	}
	public abstract Logger getLogger();

}

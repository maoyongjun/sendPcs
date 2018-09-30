package org.foxconn.util;

import org.foxconn.sendPcs.Run;

public class FileLog extends LogUtil  implements LogInterface{

	@Override
	public void logError(String msg) {
		writeXmlToLocalDisk("error",msg+System.getProperty("line.separator"));
		
	}

	@Override
	public void logInfo(String msg) {
		writeXmlToLocalDisk("info",msg+ System.getProperty("line.separator"));
	}
	
	public void logDebug(String msg){
		if("debug".equalsIgnoreCase(Run.config.getLoggerType())){
			writeXmlToLocalDisk("debug",msg+ System.getProperty("line.separator"));
		}
	}

}

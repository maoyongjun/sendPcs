package org.foxconn.util;


public class FileLog extends LogUtil  implements LogInterface{

	@Override
	public void logError(String msg) {
		writeXmlToLocalDisk("error",msg+System.getProperty("line.separator"));
		
	}

	@Override
	public void logInfo(String msg) {
		writeXmlToLocalDisk("info",msg+ System.getProperty("line.separator"));
	}


}

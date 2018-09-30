package org.foxconn.ftpupload;

import org.foxconn.util.BaseLog;
import org.foxconn.util.LogInterface;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest extends BaseLog implements LogInterface{
	
	@Test
	public void test(){
		logInfo("123");
	}
//	public static void main(String[] args) {
//		LoggerTest test = new LoggerTest();
//		test.logInfo("123");
//	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(LoggerTest.class);
	}
}

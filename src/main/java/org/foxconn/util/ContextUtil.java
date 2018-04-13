package org.foxconn.util;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextUtil {
	public static ApplicationContext getContext(){
		return new ClassPathXmlApplicationContext(new String[]{"securityConfig.xml","applicationContext.xml"}); 
	}
	
//	public static BasicDataSource getDataSourceBean(){
//		BasicDataSource ds = getContext().getBean("db9",BasicDataSource.class);
//		System.out.println(ds);
//		return ds;
//	}
}

package org.foxconn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

public class PropUtils {
	public static String LOCATION;
	public static final String CONFIG ="config.properties";
	static{
		
	      try {  
//	            String temp = URLDecoder.decode(PropUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");  
//	            LOCATION = temp.substring(1, temp.lastIndexOf('/')); 
	            LOCATION = System.getProperty("catalina.home") ;
	        } catch (Exception e) {  
	            LOCATION = "";  
	        }  
	          
	}
	public static void main(String[] args) throws Exception {
		Properties prop = getProperties(CONFIG);
		setConfigValue("H7108464", "db2");
		setConfigValue("H7108464", "db8");
	}
	 public static Properties getProperties(String filepath) throws Exception { 
			File file = new File(LOCATION+"/"+filepath);
	    	if (!file.exists()){
	    		file.createNewFile();
	    	}
	        Properties prop = new Properties();  
	        FileInputStream fis = new FileInputStream(LOCATION+"/"+filepath);  
	        prop.load(fis);  
	        return prop;  
	    }  
	      
	    public static void SaveProperties(Properties prop,String filepath) throws Exception {  
	    	File file = new File(LOCATION+"/"+filepath);
	    	if (!file.exists()){
	    		file.createNewFile();
	    	}
	        FileOutputStream fos = new FileOutputStream(LOCATION+"/"+filepath);  
	        prop.store(fos, "@author reportSystem");  
	        fos.close();  
	    }  
	  
	    public static String getConfigValue(String key) {  
	        try {  
	            Properties properties = getProperties(CONFIG);  
	            if(properties.get(key)!=null){  
	                return properties.get(key).toString();  
	            }  
	        } catch (Exception e) {  
	            System.out.println(e.getMessage());  
	        }  
	        return "";  
	    }  
	      
	    public static void setConfigValue(String key,String value){  
	        try {  
	            Properties properties = getProperties(CONFIG);  
	            properties.setProperty(key, value);  
	            SaveProperties(properties, CONFIG);  
	        } catch (Exception e) {  
	            System.out.println(e.getMessage());  
	        }  
	    }  
}

package uk.ac.bbk.cryst.netpan.common;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class PropertiesHelper {
	
	private String fileName;
	
	public PropertiesHelper(){
		this.fileName = "program.properties";
	}
	
	 public String getValue(String key, String defaultValue) throws IOException{
			Properties prop = new Properties();
			prop.load(ClassLoader.getSystemResourceAsStream("uk/ac/bbk/cryst/netpan/common/"+this.fileName));
			String value = prop.getProperty(key);
			
			if(StringUtils.isEmpty(value)) {
				return defaultValue;
			}
			
			return value; 	
	   }
	   
	   public String getValue(String key) throws IOException{
			Properties prop = new Properties();
			prop.load(ClassLoader.getSystemResourceAsStream("uk/ac/bbk/cryst/netpan/common/"+this.fileName));
			String value = prop.getProperty(key);
			
			if(StringUtils.isEmpty(value)) {
				return null;
			}
			
			return value; 	
	   }
	

}

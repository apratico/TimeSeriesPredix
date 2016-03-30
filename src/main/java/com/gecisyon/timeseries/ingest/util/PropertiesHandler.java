package com.gecisyon.timeseries.ingest.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class PropertiesHandler {
	
	private static Logger logger = Logger.getLogger(PropertiesHandler.class.getName());
    
	private String propertiesFile;
    private static Map<String, PropertiesHandler> propertiesMap = new HashMap<String, PropertiesHandler>();
    private java.util.Properties applicationProps;
    private java.io.FileInputStream in;
    
    PropertiesHandler(String url) {    	
		super();
		propertiesFile = url;
		logger.info("load " + propertiesFile + " in memory.....");
		try{
		    // read properties file
		    applicationProps = new java.util.Properties();
		    in = new java.io.FileInputStream(propertiesFile);
		    applicationProps.load(in);
		    in.close();
		} 
		catch (Exception e) {
		    logger.error(e.getMessage());
		}
    }    

    public static PropertiesHandler getInstance(String url) {
		if (!propertiesMap.containsKey(url)){
		    PropertiesHandler propsHandler = new PropertiesHandler(url);
		    propertiesMap.put(url, propsHandler);
		}
		return propertiesMap.get(url);
    }
    
    public static void refresh(String url){
    	propertiesMap.remove(url);
    	
    }

    public String getProperty(String key) {
    	return applicationProps.getProperty(key);
    }
  
}

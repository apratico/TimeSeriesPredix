package com.gecisyon.timeseries.ingest.rest;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;
import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.log4j.Logger;

import com.gecisyon.timeseries.ingest.rest.util.RestUtil;
import com.gecisyon.timeseries.ingest.util.TSConfig;

public class JWTGenerator {
	private static Logger logger = Logger.getLogger(JWTGenerator.class.getName());
	/**
	 * Generazione del token per connessione ai servizi di Predix.io
	 * @throws Exception 
	 */
	
	public enum GrantType{
		password,
		client_credentials
	}
	
	public static void requestAndSetToken() throws Exception{
		logger.info("Richiesto un nuovo token");
		TSConfig tsconf = TSConfig.getInstance();
		/**  HEADER PARAMETER  **/
		MultivaluedHashMap<String, Object> headerMap = new MultivaluedHashMap<String, Object>();
		headerMap.add("Authorization", "Basic "+tsconf.getBase64Authorization());		
		
		//String data = "grant_type=password&client_id=client_boas_3&client_secret=admin_boas_secret&username=boas_user_3&password=boas_password_3&response_type=token";
		/**  REQUEST PARAMETER  **/
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("client_id", tsconf.getCLIENT_ID());
		paramMap.put("client_secret", tsconf.getCLIENT_SECRET());
		paramMap.put("response_type", "token");
		GrantType grantType = GrantType.valueOf(tsconf.getGRANT_TYPE());
		switch (grantType) {
    		case password:
    			paramMap.put("grant_type", "password");
    			paramMap.put("username", tsconf.getUAA_USERNAME());
    			paramMap.put("password", tsconf.getUAA_PASSWORD());
    			break;
    		case client_credentials:
    			paramMap.put("grant_type", "client_credentials");    			
    			break;
    		default:
    			break;
		}
		
		JsonObject token = RestUtil.restCall(tsconf.getUAA_OAUTH_TOKEN_ADDRESS(), paramMap, null, 
				RestUtil.METHOD_POST, RestUtil.MEDIA_TYPE_FORM_URLENCODED_UTF_8, headerMap, null);
		
		logger.debug(token);
		
		if(token!=null && token.containsKey("access_token")){
			logger.info("Token ottenuto..");
			tsconf.setAUTHORIZATION(token.getString("access_token"));
		}else{
			logger.info("Token Non ottenuto..");
			throw new Exception("Impossibile Generare il token");
		}
	}
}

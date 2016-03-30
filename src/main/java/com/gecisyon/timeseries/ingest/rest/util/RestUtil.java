package com.gecisyon.timeseries.ingest.rest.util;

import java.util.Map;

import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.gecisyon.timeseries.ingest.rest.RestClient;
import com.gecisyon.timeseries.ingest.rest.RestGenericErrorException;
import com.gecisyon.timeseries.ingest.util.TSConfig;

public class RestUtil {
	private static final Logger logger = Logger.getLogger(RestUtil.class.getName());
	
	public static final String MEDIA_TYPE_JSON_UTF_8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";	
	public static final String MEDIA_TYPE_FORM_URLENCODED_UTF_8 = MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8";	
	public static final String METHOD_POST ="POST";	
	public static final String METHOD_PUT ="PUT";	
	public static final String METHOD_GET ="GET";	
	public static final String METHOD_DELETE ="DELETE";	
	public static final String TOKEN_API = "tokenAPI";
	/**
	 * 
	 * @param url
	 * @param paramMap
	 * @param pathparam
	 * @param method
	 * @param mediaType
	 * @param headerMap
	 * @param data
	 * @return
	 */
	public static JsonObject restCall(String url, Map<String, Object> paramMap, String pathparam, 
			String method, String mediaType,  MultivaluedHashMap<String, Object> headerMap, String data){
		logger.info("Effettuo una chiamata rest generica");
		Client client = null;
		JsonObject jObj = null;
		try{
			client = RestClient.getDefaultClient();
			WebTarget target = null;
			target = client.target(url);
			if(paramMap!=null && !paramMap.isEmpty()){
				for(String key : paramMap.keySet()){
					target = target.queryParam(key, paramMap.get(key));
				}
			}
			if(pathparam!=null){
				target = target.path(pathparam);
			}
			
			Response response = null;
			Builder build = null;

			if(headerMap!=null && !headerMap.isEmpty()){
				
				build =	target.request(mediaType).headers(headerMap);
				
			}
			else{
				build = target.request(mediaType);
			}
			
			if(data != null){
				response =	build.method(method, Entity.entity(data, mediaType));
				
			}
			else{
				response = build.method(method);
			}
			 
			//Se il codice non è 200, lancio l'eccezione!!
			errorStatus(response);
			jObj = response.readEntity(JsonObject.class);
			
		}
		catch (WebApplicationException e) {
			logger.error("", e);
			throw e;
		}
		finally {
			if (client != null) {
				client.close();
			}
		}
		return jObj;
	}
	
	
	/**
	 * Se il codice non è 200, lancio l'eccezione
	 * @param response
	 */
	private static void errorStatus(Response response) {
		if(response.getStatus() != Response.Status.OK.getStatusCode()) {
			throw new WebApplicationException(response);
		}
	}
	
	public static void checkLoginToken(String accessToken){
		if(accessToken==null || accessToken.trim().isEmpty() || !TSConfig.getInstance().checkLoginTokenisValid(accessToken))
    		throw new RestGenericErrorException("access_token non valido..");
	}
}

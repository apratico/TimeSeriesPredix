package com.gecisyon.timeseries.ingest.rest;


import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.json.stream.JsonGenerator;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;

public class RestClient {
	
	private static Logger logger = Logger.getLogger(RestClient.class.getName());
	
	static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){
 
	        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
	        	return true;
	        	//TODO Da eliminare questo IF e far ritornare sempre true a prescindere dall'hostname??
//	            if (hostname.equals("localhost")) {
//	                return true;
//	            }
//	            return false;
	        }
	    });
	}
	
	public static Client getDefaultClient(){
		logger.info("Recupera un client di connessione Restful standard che utilizza Jackson");
		
		/* ********************************************************************************************	*
		 * 										OPZIONI SSL												*	
		 * ********************************************************************************************	*/
		
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    @Override
			public X509Certificate[] getAcceptedIssuers(){
		    	return null;
		    }
		    
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {				
			}
		}};

		// Install the all-trusting trust manager
		SSLContext sc = null;
		try {
		    sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		} catch (Exception e) {}
		
		/* ********************************************************************************************	*
		 * 										JERSEY CLIENT											*	
		 * ********************************************************************************************	*/
		
		Client client = ClientBuilder.newBuilder()
										.sslContext(sc)
										.register(JsonProcessingFeature.class)
								        .property(JsonGenerator.PRETTY_PRINTING, true)
								        .build();
		
		return client;
	}

}
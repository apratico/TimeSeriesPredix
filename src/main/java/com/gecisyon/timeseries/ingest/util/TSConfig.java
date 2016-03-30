package com.gecisyon.timeseries.ingest.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;

import com.gecisyon.timeseries.ingest.rest.dto.TSConfigDTO;
import com.gecisyon.timeseries.ingest.websocket.ClientSocket;


public class TSConfig {
	private static Logger logger = Logger.getLogger(TSConfig.class.getName());
	private PropertiesHandler props;
	private static final String PROPERTIES_FILE = "ts.properties";
	
	private String TS_WS_ADDRESS;
	private String ORIGIN;
	private String PREDIX_ZONE_ID;
	private String AUTHORIZATION;
	private String CLIENT_ID;
	private String CLIENT_SECRET;
	private String GRANT_TYPE;
	private String UAA_USERNAME;
	private String UAA_PASSWORD;
	private String UAA_OAUTH_TOKEN_ADDRESS;
	private final int WEB_CONTAINER_PORT;
	private final int LAST_MEASURES_TO_SHOW;
	private static TSConfig instance;

	public static synchronized TSConfig getInstance() {
		if (instance == null) {
			try {
				instance = new TSConfig();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	private TSConfig() throws Exception {
		logger.info("Init delle properties.");
		PropertiesHandler handler = PropertiesHandler.getInstance(PROPERTIES_FILE);
		this.props = handler;
		this.ORIGIN = this.props.getProperty("origin");
		this.PREDIX_ZONE_ID = this.props.getProperty("predix-zone-id");
		this.TS_WS_ADDRESS = this.props.getProperty("ts.ws.address");
		this.UAA_OAUTH_TOKEN_ADDRESS = this.props.getProperty("uaa.oauth.token.address");
		this.GRANT_TYPE = this.props.getProperty("grant_type");
		this.CLIENT_ID = this.props.getProperty("client_id");
		this.CLIENT_SECRET = this.props.getProperty("client_secret");
		this.UAA_USERNAME = this.props.getProperty("uaa.username");
		this.UAA_PASSWORD = this.props.getProperty("uaa.password");
		this.WEB_CONTAINER_PORT = (this.props.getProperty("web.container.port")!=null)?Integer.parseInt(this.props.getProperty("web.container.port")):8080;	
		this.LAST_MEASURES_TO_SHOW = (this.props.getProperty("last.measures.to.show")!=null)?Integer.parseInt(this.props.getProperty("last.measures.to.show")):100;
	}	

	public String getCLIENT_ID() {
		return this.CLIENT_ID;
	}
	

	public String getUAA_USERNAME() {
		return this.UAA_USERNAME;
	}

	public String getUAA_PASSWORD() {
		return this.UAA_PASSWORD;
	}

	public int getWEB_CONTAINER_PORT() {
		return this.WEB_CONTAINER_PORT;
	}

	public String getCLIENT_SECRET() {
		return this.CLIENT_SECRET;
	}

	public String getUAA_OAUTH_TOKEN_ADDRESS() {
		return this.UAA_OAUTH_TOKEN_ADDRESS;
	}

	public String getTS_WS_ADDRESS() {
		return this.TS_WS_ADDRESS;
	}

	public String getORIGIN() {
		return this.ORIGIN;
	}

	public String getPREDIX_ZONE_ID() {
		return this.PREDIX_ZONE_ID;
	}
	
	public void setPREDIX_ZONE_ID(String predixZoneId) {
		this.PREDIX_ZONE_ID=predixZoneId;
	}

	public String getAUTHORIZATION() {
		return this.AUTHORIZATION;
	}
	
	public void setAUTHORIZATION(String newAUTH) {
		this.AUTHORIZATION=newAUTH;
	}

	public void setTS_WS_ADDRESS(String tS_WS_ADDRESS) {
		this.TS_WS_ADDRESS = tS_WS_ADDRESS;
	}

	public void setORIGIN(String oRIGIN) {
		this.ORIGIN = oRIGIN;
	}

	public void setCLIENT_ID(String cLIENT_ID) {
		this.CLIENT_ID = cLIENT_ID;
	}

	public void setCLIENT_SECRET(String cLIENT_SECRET) {
		this.CLIENT_SECRET = cLIENT_SECRET;
	}

	public void setUAA_USERNAME(String uAA_USERNAME) {
		this.UAA_USERNAME = uAA_USERNAME;
	}

	public void setUAA_PASSWORD(String uAA_PASSWORD) {
		this.UAA_PASSWORD = uAA_PASSWORD;
	}

	public void setUAA_OAUTH_TOKEN_ADDRESS(String uAA_OAUTH_TOKEN_ADDRESS) {
		this.UAA_OAUTH_TOKEN_ADDRESS = uAA_OAUTH_TOKEN_ADDRESS;
	}
	
	public String getGRANT_TYPE() {
		return GRANT_TYPE;
	}

	public void setGRANT_TYPE(String GRANT_TYPE) {
		this.GRANT_TYPE = GRANT_TYPE;
	}	

	public int getLAST_MEASURES_TO_SHOW() {
		return LAST_MEASURES_TO_SHOW;
	}

	/********  METODI DI UTILITY UN PO ZOZZI *********/
	/**
	 * @return
	 * @throws Exception
	 */
	public String getBase64Authorization() throws Exception{
		logger.info("generatione Base64 Authorization header");
		if(this.CLIENT_ID!=null && this.CLIENT_SECRET!=null){
			String auth = this.CLIENT_ID+":"+this.CLIENT_SECRET;
			return Base64.encodeAsString(auth.getBytes());	
		}
		throw new Exception("client_id e/o client_secret invalidi");
	} 
	
	/**
	 * 
	 * @param loginToken
	 * @return
	 */
	public boolean checkLoginTokenisValid(String loginToken){
		logger.info("controllo validità login_token");
		try {
			return getBase64Authorization().equals(loginToken);
		} catch (Exception e) {
			logger.error("", e);
		}
		return false;
	}
	

	
	public static boolean reconfigure(TSConfigDTO configDTO) {
		
		try {
			FileInputStream in = new FileInputStream(PROPERTIES_FILE);
		
			Properties props = new Properties();
			props.load(in);
			in.close();
			
			FileOutputStream out = new FileOutputStream(PROPERTIES_FILE);
			if(configDTO.getOrigin()!=null && !configDTO.getOrigin().trim().isEmpty())
				props.setProperty("origin", configDTO.getOrigin());
			if(configDTO.getPredix_zone_id()!=null && !configDTO.getPredix_zone_id().trim().isEmpty())
				props.setProperty("predix-zone-id", configDTO.getPredix_zone_id());
			if(configDTO.getTs_ws_address()!=null && !configDTO.getTs_ws_address().trim().isEmpty())
				props.setProperty("ts.ws.address", configDTO.getTs_ws_address());
			if(configDTO.getUaa_oauth_token_address()!=null && !configDTO.getUaa_oauth_token_address().trim().isEmpty())
				props.setProperty("uaa.oauth.token.address", configDTO.getUaa_oauth_token_address());
			if(configDTO.getClient_id()!=null && !configDTO.getClient_id().trim().isEmpty())
				props.setProperty("client_id", configDTO.getClient_id());
			if(configDTO.getClient_secret()!=null && !configDTO.getClient_secret().trim().isEmpty())
				props.setProperty("client_secret", configDTO.getClient_secret());
			if(configDTO.getUaa_username()!=null && !configDTO.getUaa_username().trim().isEmpty())
				props.setProperty("uaa.username", configDTO.getUaa_username());
			if(configDTO.getUaa_password()!=null && !configDTO.getUaa_password().trim().isEmpty())
				props.setProperty("uaa.password", configDTO.getUaa_password());
			if(configDTO.getGrant_type()!=null && !configDTO.getGrant_type().trim().isEmpty())
				props.setProperty("grant_type", configDTO.getGrant_type());
			if(configDTO.getLast_measures_to_show()!=0)
				props.setProperty("last.measures.to.show", String.valueOf(configDTO.getLast_measures_to_show()));
			
			props.store(out, null);
			out.close();
			//Refresh delle variabili e delle connessioni attive
			PropertiesHandler.refresh(PROPERTIES_FILE);
			instance = new TSConfig();
			ClientSocket.getInstance().forceReconnect();
			return true;
		} catch ( Exception e) {
			logger.error(e.getMessage());
			logger.error("", e);
			return false;
		}
	}
	
	
}

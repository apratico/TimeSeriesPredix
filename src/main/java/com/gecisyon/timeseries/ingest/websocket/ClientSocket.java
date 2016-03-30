package com.gecisyon.timeseries.ingest.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import com.gecisyon.timeseries.ingest.rest.JWTGenerator;


/**
 * Classe che si occupa di gestire la connessione verso il websocket con il Proxy
 * @author Fabio Terella
 */
public class ClientSocket {
	
	private static final Logger logger = Logger.getLogger(ClientSocket.class.getName());
	
	private static ClientSocket instance;
	private Session session;
	private String address;
	
	private ClientSocket(){
		session = null;
	}
	
	public static synchronized ClientSocket getInstance(){
		if(instance == null)
			instance = new ClientSocket();
		return instance;
	}
	
	public void setSession(Session session){
		this.session = session;
	}
	
	/**
	 * Funzione che si occupa di connettersi al websocket del proxy identificandosi con il clientId passato come parametro di
	 * path
	 * @param address identificativo del client di connessione
	 * @return true in caso di successo, false altrimenti
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws DeploymentException 
	 */
	public boolean connect(String address) {
		logger.debug("Richiesta la connessione ad un websocket");
		
		try{	
			JWTGenerator.requestAndSetToken();
			ClientManager client = ClientManager.createClient();
			
			URI websocketURI = new URI(address);
			
			client.connectToServer(new Endpoint(), websocketURI);
			this.address = address;
			logger.debug("Connesso al WS:"+address);
			return true;
		}		
		catch(DeploymentException ex){
			logger.error("Verificare Credenziali");
			logger.error("", ex);
			
			return false;
		}
		
		catch(Exception e){
			logger.error("Exception Non posso connettermi con il protocollo websocket al Proxy!!");
			logger.error("", e);
			return false;
		}
	}
	
	/**
	 * Funzione che forza la disconnessione dal websocket
	 */
	public void disconnect(){
		logger.debug("Richiesta la chiusura della connessione del websocket!!");
		
		try{
			if(session != null && session.isOpen())
				session.close();			
		}
		catch(Exception e){
			logger.error("Non posso chiudere la connessione con il websocket!!");
		}
	}
	
	/**
	 * Funzione che verifica se al momento il websocket esiste ed � attivo
	 * @return true in caso di websokect attivo e aperto, false altrimenti
	 */
	public boolean isSocketOpen(){
		if(session != null)
			return session.isOpen();
		
		return false;		
	}
	
	/**
	 * Funzione che forza la riconnessione al proxy con i parametri già presenti
	 * @return true in caso di successo, false altrimenti
	 */
	public boolean reconnect(){
		if(this.address != null && session != null && !session.isOpen())
			return this.connect(this.address);
		
		return false;
	}
	
	public boolean forceReconnect(){
		disconnect();
		return reconnect();
	}
	
	public boolean sendMessage(String message){
		boolean retValue = false;		
		logger.debug(message);
		try {				
			if(isSocketOpen()){				
				session.getBasicRemote().sendText(message);
				session.getBasicRemote().flushBatch();
				retValue = true;
			}else{
				forceReconnect();
			}				
		} 
		catch (Exception e) {
			logger.error("Errore nell'invio del messaggio", e);
		}
		
		return retValue;
	}
	
}
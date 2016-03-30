package com.gecisyon.timeseries.ingest.websocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.log4j.Logger;

@ClientEndpoint(configurator = ClientConfigurator.class)
public class Endpoint {

	private static final Logger logger = Logger.getLogger(Endpoint.class.getName());

	@OnOpen
	public void onOpen(Session session) {
		ClientSocket.getInstance().setSession(session);
//		try {
//			String name = "Boas";
//			System.out.println("Sending message to endpoint: " + name);
//			session.getBasicRemote().sendText(name);
//		} catch (IOException ex) {
//			logger.error("IOException: ", ex);
//		}

	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("onClose "+closeReason.getReasonPhrase());
		ClientSocket.getInstance().reconnect();

	}

	@OnMessage
	public void onMessage(String message) {
		logger.debug("Arrivato il json --> " + message);

	}
}

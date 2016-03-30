package com.gecisyon.timeseries.ingest.websocket;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.gecisyon.timeseries.ingest.util.TSConfig;

public class IngestionClient implements Runnable{
	private static final CountDownLatch closeLatch = new CountDownLatch(1);
	private static Logger logger = Logger.getLogger(IngestionClient.class.getName());
	@Override
	public void run() {
		logger.debug("Avvio il Thread per la connessione al WS");
		try {
			if(!ClientSocket.getInstance().isSocketOpen()){
				if(ClientSocket.getInstance().connect(TSConfig.getInstance().getTS_WS_ADDRESS())){
					logger.debug("connesso!");
					logger.debug("mi metto in awai()");
					closeLatch.await();
				}else{
					throw new InterruptedException("Errore di connessione...");
				}
				
			}			
		} catch (InterruptedException e) {
			logger.error("Errore:"+e.getMessage());
			logger.error("",e);
			
		}
	}

}

package com.gecisyon.timeseries.ingest;

import io.swagger.jaxrs.config.BeanConfig;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gecisyon.timeseries.ingest.rest.RestServer;
import com.gecisyon.timeseries.ingest.rest.resource.EntryPoint;
import com.gecisyon.timeseries.ingest.simulator.MachineDataExecutor;
import com.gecisyon.timeseries.ingest.websocket.ClientSocket;
import com.gecisyon.timeseries.ingest.websocket.IngestionClient;

public class AppIngestion {
	
	private static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	private static Logger logger = Logger.getLogger(AppIngestion.class.getName());
	private static Thread mainIngestionClient;
	private static Thread mainWebContainer;
	private static Thread mainMDataExec;
	
	public static void main( String[] args )
    {
		/* ************************************************************************************************	*
		 * 										INIZIALIZZO LOG4J											*
		 * ************************************************************************************************	*/
		
		try{
			Properties applicationProps = new java.util.Properties();
			FileInputStream in = new java.io.FileInputStream(LOG4J_PROPERTIES_FILE);
			applicationProps.load(in);
			in.close();
			PropertyConfigurator.configure(applicationProps);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		/* ************************************************************************************************	*
		 * 										START WEB CONTAINER JETTY									*
		 * ************************************************************************************************	*/
		
		logger.info("Star Jetty Server");
		mainWebContainer = new Thread((Runnable)new RestServer(), "JettyServer");
		mainWebContainer.start();
		
		/* ************************************************************************************************	*
		 * 										START WS CLIENT 											*
		 * ************************************************************************************************	*/
		logger.info("Lancio il Thread per la connessione al WS del time series");
		mainIngestionClient = new Thread((Runnable)new IngestionClient(), "IngestionWSClient");
		mainIngestionClient.start();				
		
		
		
		/* ************************************************************************************************	*
		 *									 START SCRITTURE SUL TIMESERIES									*
		/* ************************************************************************************************ */		
		int attempt = 0;
		while(attempt<25){
			attempt++;
			if(ClientSocket.getInstance().isSocketOpen()){	
				logger.info("Lancio il Thread per la scrittura dei dati nel time series");
				mainMDataExec = new Thread((Runnable)new MachineDataExecutor(), "MachineDataExecutor");			
				mainMDataExec.start();
				break;
			}else{
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
					logger.error("", e);
				}
				logger.error("non connesso...  attempt:"+attempt);
			}
		}
		if(attempt>=25){
			logger.error("Conessione non riuscita... VERIFICARE PARAMETRI!");
		}
    }
}

package com.gecisyon.timeseries.ingest.simulator;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gecisyon.timeseries.ingest.simulator.model.IngestionData;
import com.gecisyon.timeseries.ingest.simulator.model.MessageData;
import com.gecisyon.timeseries.ingest.simulator.model.Sensor;
import com.gecisyon.timeseries.ingest.websocket.ClientSocket;

public class MachineDataGenerator implements Runnable {
	
	private static Logger logger = Logger.getLogger(MachineDataGenerator.class.getName());
	private Sensor sensor;
	private String name;
	//private DataType datatype;
	private int low;
	private int high;
	private int alertLow;
	private int alertHigh;
	private boolean isAlertType;
	private int alarmMeasures;
	private long sample;
	private Map<String,String> attributes;
	private final ObjectMapper mapper = new ObjectMapper();	
	
	public MachineDataGenerator(Sensor sensor){
		logger.debug("inizializza thread generatore misurazioni");
		this.sensor = sensor;
		
				
	}	
	
	@Override
	public void run() {
		logger.debug("Run del thread generatore misurazioni per sensore: "+sensor.getSensorId());
		//acquisizione di un semaforo per il sensore da lavorare
		MachineDataManager.getInstance().acquireSensorSemaphore(sensor);		
		init();	
		int numOfMeasures = (this.isAlertType) ? this.alarmMeasures : 1;
		
		int measureNum = 0;
		while(measureNum < numOfMeasures){
//			System.out.println("measureNum: "+measureNum+ " < " + numOfMeasures + ": numOfMeasures");
			measureNum++;
			long epochTime = new Date().getTime();
			
			//popolo e genero datapoint
			List<Object> datapoints = new ArrayList<Object>();
			datapoints.add(epochTime);
			datapoints.add(getRandomValue()); 
			if(isAlertType)
				datapoints.add(1);
			
			//aggiungo datapoints generati dai points
			List<Object> points = new ArrayList<Object>();
			points.add(datapoints);
			
			//popolo attributi e altre informazioni
			IngestionData data = new IngestionData();		
			data.setAttributes(this.attributes);
			data.setName(this.name);
			data.setDatapoints(points);
			
			List<IngestionData> body = new ArrayList<IngestionData>();
			body.add(data);
			
			MessageData msgData = new MessageData();
			msgData.setMessageId(epochTime+"_"+data.getName());
			msgData.setBody(body);
			
			//Invio i dati al servizio di Predix.io per la dataIngestion
			try {			
				ClientSocket.getInstance().sendMessage(mapper.writeValueAsString(msgData));
			} catch (JsonProcessingException e) {
				logger.error(e.getMessage());
				logger.error("",e);
			}
			
			//aggiungo le misurazione in memeoria per fini statistici..
			MachineDataManager.getInstance().addMeasures(sensor, datapoints);
			
			// sleeep per il sample time e rilascio del semaforo
			try{
				Thread.sleep(this.sample);
			}catch (Exception e){
				logger.error(e.getMessage());
				logger.error("",e);
			}
		}
		//rilascio del semaforo per i Thread successivi
		MachineDataManager.getInstance().releaseTokenSemaphore(sensor);
	}



	private void init() {
		this.name = sensor.getSensorId();
		//this.datatype = DataType.valueOf(sensor.getOum());
		this.low = sensor.getRangeMin();
		this.high = sensor.getRangeMax();
		this.alertLow = sensor.getAlertMin();
		this.alertHigh = sensor.getAlertMax();
		this.attributes = new HashMap<String, String>();
		this.sample = sensor.getSample();
		this.alarmMeasures = sensor.getAlarmMeasures();
		this.attributes.put("asset_id", sensor.getAssetId());
		this.attributes.put("component_id", sensor.getComponentId());
		this.attributes.put("sensor_id", sensor.getSensorId());
		this.isAlertType = (MachineDataManager.getInstance().getAlarmToGenerate(sensor)!=null);
	}
	
	/***
	 * Genera un rondom in base le specifiche definite
	 * @return
	 */
	private double getRandomValue(){
		int lowerBound; 
		int upperBound;
		if(this.isAlertType){
			if(new Random().nextBoolean()){
				//genero valore nella parte bassa del range fuori misura
				upperBound = this.alertLow;
				lowerBound = this.low;
			}else{
				//genero valore nella parte alta del range fuori misura
				upperBound = this.high;
				lowerBound = this.alertHigh;
			}
		}else{
			upperBound = this.alertHigh;
			lowerBound = this.alertLow;
		}
		/* 4 debug... */
		double rdm = new Random().nextDouble();
		final double dbl = rdm * (upperBound -lowerBound) + lowerBound;
		logger.warn("Random --> ("+upperBound+":"+lowerBound+") : "+dbl);
		return dbl;
	}
	
	
	public enum DataType {
		g,
		kw,
		rpm,
		volt,
		degree,
		celsius,
		psi,
		hpa,
		percentage,
		kt,
		mm,
		number
	}

}

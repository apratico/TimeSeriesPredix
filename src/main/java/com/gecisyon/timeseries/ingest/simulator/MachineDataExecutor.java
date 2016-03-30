package com.gecisyon.timeseries.ingest.simulator;

import java.util.Set;

import org.apache.log4j.Logger;

import com.gecisyon.timeseries.ingest.simulator.MachineDataManager.PoolStatus;
import com.gecisyon.timeseries.ingest.simulator.model.Sensor;

public class MachineDataExecutor implements Runnable{
	private static Logger logger = Logger.getLogger(MachineDataExecutor.class.getName());
	
	@Override
	public void run() {
		logger.debug("Running Thread per la routine di inserimento ");
		Set<Sensor> sensorList = MachineDataManager.getInstance().getSensorList();
		if(sensorList!=null){
			while( MachineDataManager.getInstance().isRunMachineDataExecutor()){
				//se la coda è piena faccio uno sleep di 1 secondo e salto il ciclo while
				if(!MachineDataManager.getInstance().canAddJobtoQueue() 
						|| MachineDataManager.getInstance().getStatus().equals(PoolStatus.PAUSED) || sensorList.size()==0){
					logger.debug("Coda Piena e/o lista sensori vuota e/o Status --> "+MachineDataManager.getInstance().getStatus());
					try {
						Thread.sleep(1000l);
						sensorList = MachineDataManager.getInstance().getSensorList();
					} catch (InterruptedException e) {					
						e.printStackTrace();
					}					
					continue;
				}
				
				for (Sensor sensor : sensorList) {
					logger.debug("Sensor Id:"+ sensor.getSensorId());					
					MachineDataManager.getInstance().setJob(sensor);				
				}
				
			}
		}else{
			logger.error("Lista sensori vuota...");
		}
		
		
	}
	

	
	

}

package com.gecisyon.timeseries.ingest.simulator;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gecisyon.timeseries.ingest.rest.dto.SensorDTO;
import com.gecisyon.timeseries.ingest.rest.dto.assembler.SensorAssembler;
import com.gecisyon.timeseries.ingest.simulator.model.AlarmInfo;
import com.gecisyon.timeseries.ingest.simulator.model.Sensor;
import com.gecisyon.timeseries.ingest.simulator.util.PausableThreadPoolExecutor;
import com.gecisyon.timeseries.ingest.util.TSConfig;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Manager dei task per la generazione dei dati per sensori
 * @author Alessandro
 *
 */
public class MachineDataManager {
	private static Logger logger = Logger.getLogger(MachineDataManager.class.getName());
	private static MachineDataManager instance;	
	private PausableThreadPoolExecutor measuresPool;
	private PoolStatus status;
	private final Map<Sensor, LinkedBlockingQueue<AlarmInfo>> alarmsToGenerate;
	private Set<Sensor> sensorList; 
	private Map<Sensor, List<Object>> lastMeasures;
	private boolean runMachineDataExecutor;
	//sensorSemaphoreMap è statico nel caso di nuove istanze di MDM non si creano errrori di orchestrazione
	private static Hashtable<Sensor, Semaphore> sensorSemaphoreMap;
	private BlockingQueue<Runnable> measuresQueue;
	
	private MachineDataManager(){
		logger.info("Instanzio il pool per le misurazioni");
		//Istanzio il pool per le measures
		this.measuresQueue = new LinkedBlockingQueue<Runnable>();
		this.measuresPool = new PausableThreadPoolExecutor(1, 10, 60, TimeUnit.SECONDS, measuresQueue,
				new ThreadFactory() {
		            public Thread newThread(Runnable r) {
		                Thread t = new Thread(r);
		                t.setName("MeasuresThread");
		                return t;
		            }// End method
		});
		this.measuresPool.allowCoreThreadTimeOut(false);	
		this.pause();
		this.status = PoolStatus.PAUSED;			
		lastMeasures = new Hashtable<Sensor, List<Object>>();
		this.alarmsToGenerate = new Hashtable<Sensor,LinkedBlockingQueue<AlarmInfo>>();
		this.runMachineDataExecutor = true;
		this.sensorList = new HashSet<Sensor>();		
		MachineDataManager.sensorSemaphoreMap = new Hashtable<Sensor, Semaphore>();
		this.setSensorListInMem();
		this.addSensorsSemaphore(this.sensorList);		
			
		
	}
	
	public static synchronized MachineDataManager getInstance(){
		if(instance == null)
			instance = new MachineDataManager();
		return instance;
	}
	
	
	public synchronized void pause(){
		logger.info("metto in Pausa il pool per le misurazioni ");
		measuresPool.pause();
		this.status = PoolStatus.PAUSED;
	}
	
	public synchronized void resume(){
		logger.info("Resumo il pool per le misurazioni ");
		measuresPool.resume();
		this.status = PoolStatus.ACTIVE;
	}
	
	public enum PoolStatus {
		INACTIVE,
		ACTIVE,
		PAUSED
	}

	public PoolStatus getStatus() {
		return status;
	}
	
	public boolean canAddJobtoQueue(){
		logger.debug("controllo se è possibile accodare Job al ThreadPool");
		//aggiungere logica per il calcolo della grandezza massima della coda
		if(this.measuresQueue!=null && this.measuresQueue.size()<400){
			logger.debug("measuresQueue.size() --> "+this.measuresQueue.size());
			return true;
		}
		return false;
	}	
	
	public void setJob(Sensor sensor){
		logger.debug("Aggiungo un Job per il pool per le misurazioni..sensor: "+sensor.getSensorId());
		measuresPool.execute((Runnable) new MachineDataGenerator(sensor));		
	}
	
	public boolean isRunMachineDataExecutor() {
		return runMachineDataExecutor;
	}

	public void setRunMachineDataExecutor(boolean runMachineDataExecutor) {
		this.runMachineDataExecutor = runMachineDataExecutor;
	}
	

	public Set<Sensor> getAlarmsToGenerate() {
		logger.info("Richiesta la lista degli allarmi in coda..");
		return Sets.newHashSet(alarmsToGenerate.keySet().iterator());
	}
	
	public AlarmInfo getAlarmToGenerate(Sensor sensor) {
		logger.info("Richiesto l'allarme da generare..");
		if(alarmsToGenerate.isEmpty() || !alarmsToGenerate.containsKey(sensor))
			return null;
	
		return alarmsToGenerate.get(sensor).poll();
	}
	
	/**
	 * Predicate che filtra la lista di {@link DashboardDcy} in base all'id del {@link ContainerDsh}
	 * @param idint* @return
	 */
	private Predicate<Sensor> byAlarmType(final int alarmType) {
		return new Predicate<Sensor>() {

			@Override
			public boolean apply(Sensor sensor) {
				return sensor.getAlarmType()==alarmType;
			}
		};
	}
	
	public void setAlarmToGenerate(int alarmType) {
		logger.info("Set di un nuovo allarme Type:" + alarmType);
		try {
			ImmutableList<Sensor> sensorToAlarm = FluentIterable.from(sensorList).filter(byAlarmType(alarmType)).toList();
			AlarmInfo alarmInfo = AlarmInfo.builder()
		    			.epochTime(new Date().getTime())
		    			.alarmType(alarmType)
		    			.build();
			for (Sensor sensor : sensorToAlarm) {
				if(!this.alarmsToGenerate.containsKey(sensor)){
					LinkedBlockingQueue<AlarmInfo> lbq = new  LinkedBlockingQueue<AlarmInfo>();
					this.alarmsToGenerate.put(sensor, lbq);
				}
				this.alarmsToGenerate.get(sensor).put(alarmInfo);
			}
			

		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			logger.error("",e);
		}
	}

	public Set<Sensor> getSensorList() {
		return sensorList;
	}
	
	

	/**
	 * popolo la mappa dei sensori in memoria leggendo da un file json
	 */
	public void setSensorListInMem() {		
		logger.info("Init della lista dei sensori in memoria");
		try{
			FileInputStream in = new FileInputStream("sensors.json");
			ObjectMapper mapper = new ObjectMapper();
			List<SensorDTO> sensorDtoList = mapper.readValue(in, new TypeReference <List<SensorDTO>>(){});
			in.close();
			this.sensorList = new HashSet<Sensor>(new SensorAssembler().deAssembly(sensorDtoList));
			//Reinit del pool in base il numero di sensori
			int poolSize = this.sensorList.size();
			this.measuresPool.setCorePoolSize(poolSize);
			this.measuresPool.setMaximumPoolSize(poolSize);
			//svuoto la coda 
			this.measuresQueue.clear();
			//aggiungo eventuali semafori per i nuovi sensori
			this.addSensorsSemaphore(this.sensorList);
		}catch(Exception e){
			logger.error(e.getMessage());
			logger.error("",e);
		}		
	}
	
	public void configSensorList(List<SensorDTO> sensorDtoList){
		try (FileWriter file = new FileWriter("sensors.json")) {
			ObjectMapper mapper = new ObjectMapper();
			file.write(mapper.writeValueAsString(sensorDtoList));
			file.flush();
			file.close();
			logger.warn("Successfully Copied JSON Object to File...");
			logger.warn("\nJSON Object: " + mapper.writeValueAsString(sensorDtoList));
			setSensorListInMem();
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("",e);
		}
	}
	
	private synchronized void addSensorSemaphore(Sensor sensor){
		if(!sensorSemaphoreMap.containsKey(sensor))
			sensorSemaphoreMap.put(sensor, new Semaphore(1, true));
	}
	
	private synchronized void addSensorsSemaphore(Set<Sensor> sensors){
		for (Sensor sensor : sensors) {
			addSensorSemaphore(sensor);
		}
	}
	
	/**
	 * 
	 * @param sensor
	 * @return
	 */
	public List<Object> getLastMesures(Sensor sensor){
		logger.debug("Richieste le ultimi misurazione per un determinato sensore");
		if(this.lastMeasures.containsKey(sensor))
			return this.lastMeasures.get(sensor);
		
		return null;
	}
	
	/**
	 * 
	 * @param sensor
	 * @param points
	 */
	public void addMeasures(Sensor sensor, List<Object> points){
		logger.debug("Aggiungo l'ultima misurazione in memoria");
		//Se non è presenet un brackets lo aggiungo
		if(!this.lastMeasures.containsKey(sensor)){
			List<Object> linkedPoints = new LinkedList<Object>();
			this.lastMeasures.put(sensor,linkedPoints);
		}
		//aggiungo ultimi dati
		this.lastMeasures.get(sensor).add(points);
		//rimuovo i più vecchi
		if(this.lastMeasures.get(sensor).size() > TSConfig.getInstance().getLAST_MEASURES_TO_SHOW())
			((LinkedList<Object>)this.lastMeasures.get(sensor)).removeFirst();
	}
	/**
	 * Funzione che permette di acquisire il privilegio di utilizzare un sensor. L'attesa è di tipo
	 * FIFO
	 * @param sensor
	 */
	public synchronized void acquireSensorSemaphore(Sensor sensor){
		logger.debug("acquisito semaforo per sensor:"+sensor.getSensorId());
		Semaphore semaphore = sensorSemaphoreMap.get(sensor);
		semaphore.acquireUninterruptibly();
		return;
	}
	
	/**
	 * Funzione che permette di rilasciare il semaforo di un Sensore.
	 * @param sensor
	 */
	public void releaseTokenSemaphore(Sensor sensor){
		logger.debug("rilasciato semaforo per sensor:"+sensor.getSensorId());
		Semaphore semaphore = sensorSemaphoreMap.get(sensor);
		semaphore.release();
		return;
	}
	
	/* ******************************************************************** */
	/*					.... N O T -- T O -- U S E ....						*/
	
	public void stop(){
		logger.info("Fermo il pool per le misurazioni ");
		measuresPool.shutdown();
	}
	
	public boolean isTerminated(){
		logger.info("Verifico se il pool per le misurazioni  ha terminato i suoi job");
		if(measuresPool.isTerminated())
			return true;
		return false;
	}

	public void forceStopAndReinit(){
		logger.info("Forzo lo spegnimento e il re-init del pool per le misurazioni");
		try {
			measuresPool.shutdownNow();
			while(!measuresPool.isTerminated()){				
					Thread.sleep(2000l);				
			}
		} catch (InterruptedException e) {
			logger.error("",e);
		}
		instance = new MachineDataManager();
		instance.setSensorListInMem();
	}
	
	/*																		*/
	/* ******************************************************************** */
		

}



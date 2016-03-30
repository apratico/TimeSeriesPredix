package com.gecisyon.timeseries.ingest.rest.dto.assembler;

import java.util.List;

import com.gecisyon.timeseries.ingest.rest.dto.SensorDTO;
import com.gecisyon.timeseries.ingest.simulator.model.Sensor;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class SensorAssembler {

	public SensorDTO assembly(Sensor sensor){
		SensorDTO sensorDTO = new SensorDTO();
		if(sensor==null)
			return sensorDTO;
		
		sensorDTO.setAlert_max(sensor.getAlertMax());
		sensorDTO.setAlert_min(sensor.getAlertMin());
		sensorDTO.setAsset_id(sensor.getAssetId());
		sensorDTO.setAvg(sensor.getAvg());
		sensorDTO.setComponent_ds(sensor.getComponentDs());
		sensorDTO.setComponent_id(sensor.getComponentId());
		sensorDTO.setOum(sensor.getOum());
		sensorDTO.setRange_max(sensor.getRangeMax());
		sensorDTO.setRange_min(sensor.getRangeMin());
		sensorDTO.setSample(sensor.getSample());
		sensorDTO.setSensor_ds(sensor.getSensorDs());	
		sensorDTO.setSensor_id(sensor.getSensorId());
		sensorDTO.setAlarm_type(sensor.getAlarmType());
		sensorDTO.setAlarm_measures(sensor.getAlarmMeasures());
		return sensorDTO;
	}
	
	public List<SensorDTO> assembly(List<Sensor> sensorList){		
		return FluentIterable.from(sensorList).transform(toSensorDTO()).toList();	
	}	
	
	 private Function<Sensor, SensorDTO> toSensorDTO() {
		return new Function<Sensor, SensorDTO>() {
			@Override
			public SensorDTO apply(Sensor input) {
				return assembly(input);
			}
		};
	}
	
	public Sensor deAssembly(SensorDTO sensorDTO){
		Sensor sensor = new Sensor();
		if(sensorDTO==null)
			return sensor;
		
		sensor.setAlertMax(sensorDTO.getAlert_max());
		sensor.setAlertMin(sensorDTO.getAlert_min());
		sensor.setAssetId(sensorDTO.getAsset_id());
		sensor.setAvg(sensorDTO.getAvg());
		sensor.setComponentDs(sensorDTO.getComponent_ds());
		sensor.setComponentId(sensorDTO.getComponent_id());
		sensor.setOum(sensorDTO.getOum());
		sensor.setRangeMax(sensorDTO.getRange_max());
		sensor.setRangeMin(sensorDTO.getRange_min());
		sensor.setSample(sensorDTO.getSample());
		sensor.setSensorDs(sensorDTO.getSensor_ds());
		sensor.setSensorId(sensorDTO.getSensor_id());
		sensor.setAlarmType(sensorDTO.getAlarm_type());
		sensor.setAlarmMeasures(sensorDTO.getAlarm_measures());
		return sensor;
	}
	
	public List<Sensor> deAssembly(List<SensorDTO> sensorList){		
		return FluentIterable.from(sensorList).transform(toSensor()).toList();	
	}	
	
	 private Function<SensorDTO, Sensor> toSensor() {
		return new Function<SensorDTO, Sensor>() {
			@Override
			public Sensor apply(SensorDTO input) {
				return deAssembly(input);
			}
		};
	}
	
}

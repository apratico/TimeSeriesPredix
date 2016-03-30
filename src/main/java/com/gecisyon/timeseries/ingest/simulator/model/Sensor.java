package com.gecisyon.timeseries.ingest.simulator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of={"assetId", "componentId", "sensorId"})
@Builder
public class Sensor {
	//ASSET_ID	COMPONENT_ID	COMPONENT_DS	SENSOR_ID	
	//SENSOR_DS	SENSOR_LONG_DESC	UOM	RANGE_MIN	ALERT_MIN	AVG	ALERT_MAX	RANGE_MAX	Campionamento
	private String assetId;
	private String componentId;
	private String componentDs;
	private String sensorId;
	private String sensorDs;
	private String oum;
	private Integer rangeMin;
	private Integer rangeMax;
	private Integer avg;
	private Integer alertMax;
	private Integer alertMin;
	private Long sample;
	private int alarmType;
	private int alarmMeasures;

}

package com.gecisyon.timeseries.ingest.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of={"asset_id", "component_id", "sensor_id"})
@Builder
public class SensorDTO {
	private String asset_id;
	private String component_id;
	private String component_ds;
	private String sensor_id;
	private String sensor_ds;
	private String oum;
	private Integer range_min;
	private Integer range_max;
	private Integer avg;
	private Integer alert_max;
	private Integer alert_min;
	private Long sample;
	private int alarm_type;
	private int alarm_measures;
	private Object last_measures;

}

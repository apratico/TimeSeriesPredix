package com.gecisyon.timeseries.ingest.simulator.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngestionData {

	private String name;
	private List<Object> datapoints;
	private Map<String,String> attributes;
	
	 
}

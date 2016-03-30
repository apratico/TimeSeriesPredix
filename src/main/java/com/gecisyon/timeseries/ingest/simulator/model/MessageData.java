package com.gecisyon.timeseries.ingest.simulator.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageData {	
	private String messageId;
	private List<IngestionData> body;
}

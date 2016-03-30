package com.gecisyon.timeseries.ingest.rest.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gecisyon.timeseries.ingest.rest.dto.SensorDTO;
import com.gecisyon.timeseries.ingest.rest.dto.assembler.SensorAssembler;
import com.gecisyon.timeseries.ingest.rest.util.RestUtil;
import com.gecisyon.timeseries.ingest.simulator.MachineDataManager;
import com.gecisyon.timeseries.ingest.simulator.model.Sensor;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

@Path("/alarm")
@Api(value="Alarm API")
public class AlarmR {
	private static Logger logger = Logger.getLogger(AlarmR.class.getName());
	
	/**
     * Imposta un nuovo allarme per il codice specificato
     * @param accessToken,alarmType
     * @return
     */
    @GET
    @Path("/{alarmType}")
    @Produces("application/json;charset=UTF-8")
    @ApiOperation(
			value = "Set an alarm on system")
	@ApiResponses(value = {
			 @ ApiResponse(code = 204, message = "Successful alarm generated!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public void generateAlarm(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true) 
    			@QueryParam("access_token") String accessToken, 
    		@ApiParam(value="Alarm Type Id valid options (1-2) --> [{1:[\"SENSOR_13\"] ,2:[\"SENSOR_02\",\"SENSOR_03\"]}]", required=true) 
    			@PathParam("alarmType") final int alarmType) {    	
    	RestUtil.checkLoginToken(accessToken);
    	MachineDataManager.getInstance().setAlarmToGenerate(alarmType);
    }
    
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
			value = "Get alarm info")
	@ApiResponses(value = {
			 @ ApiResponse(code = 204, message = "Successful alarm info return!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public JsonObject getAlarmInfo(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true) 
    			@QueryParam("access_token") String accessToken) {    	
    	RestUtil.checkLoginToken(accessToken);
    	Map<Integer, Set<SensorDTO>> ret = new Hashtable<>();
    	JsonObject object = null;
		try {
			ImmutableList<Sensor> sensorAlarmed = FluentIterable.from(MachineDataManager.getInstance().getSensorList()).filter(byAlarm()).toList();
			for (Sensor sensor : sensorAlarmed) {
				int key = sensor.getAlarmType();
				if(!ret.containsKey(key)){
					Set<SensorDTO> appoSenList = new HashSet<SensorDTO>(); 
					ret.put(key, appoSenList);
				}
				ret.get(key).add(new SensorAssembler().assembly(sensor));
			}
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("###########\n"+mapper.writeValueAsString(ret));
			
			JsonReader jsonReader = Json.createReader(new StringReader(mapper.writeValueAsString(ret)));
			object = jsonReader.readObject();
			jsonReader.close();


		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("",e);
		}
    	
    	return object;
    }
    	
	private Predicate<Sensor> byAlarm() {
		return new Predicate<Sensor>() {

			@Override
			public boolean apply(Sensor sensor) {
				return sensor.getAlarmType() > 0;
			}
		};
	}
}

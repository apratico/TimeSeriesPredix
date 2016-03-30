package com.gecisyon.timeseries.ingest.rest.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gecisyon.timeseries.ingest.rest.dto.SensorDTO;
import com.gecisyon.timeseries.ingest.rest.dto.assembler.SensorAssembler;
import com.gecisyon.timeseries.ingest.rest.util.RestUtil;
import com.gecisyon.timeseries.ingest.simulator.MachineDataManager;
import com.gecisyon.timeseries.ingest.simulator.model.Sensor;

@Path("/sensors")
@Api(value="Sensors API")
public class SensorR {
	private static Logger logger = Logger.getLogger(SensorR.class.getName());
	/**
     * Ritorna la lista dei sensori disponibili
     * @param accessToken
     * @return List<SensorDTO>
     */
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8")
    @ApiOperation(
			value = "Get sensors list")
	@ApiResponses(value = {
			 @ ApiResponse(code = 200, message = "Successful list of sensors generated!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public List<SensorDTO> getSensors(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true)
    			@QueryParam("access_token") String accessToken
    		){
    	logger.info("Invocato path @get /sensor");
    	RestUtil.checkLoginToken(accessToken);    	
    	return new SensorAssembler().assembly(new ArrayList<Sensor>(MachineDataManager.getInstance().getSensorList()));    		
    }
    
    
   
    /**
     * Ritorna la lista dei sensori disponibili
     * @param accessToken
     * @return List<SensorDTO>
     */
    @GET
    @Path("/with_measures")
    @Produces("application/json;charset=UTF-8")
    @ApiOperation(
			value = "Get sensors list with measures")
	@ApiResponses(value = {
			 @ ApiResponse(code = 200, message = "Successful list of sensors generated!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public JsonArray getSensorsWithMeasures(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true)
    			@QueryParam("access_token") String accessToken
    		){
    	logger.info("Invocato path @get /sensor");
    	RestUtil.checkLoginToken(accessToken);
    	JsonArrayBuilder resultBuilder = Json.createArrayBuilder();
    	
		for (Sensor sensor : MachineDataManager.getInstance().getSensorList()) {
			
				SensorDTO ret = new SensorAssembler().assembly(sensor);
				List<Object> appo = MachineDataManager.getInstance().getLastMesures(sensor);
				for (Object object : appo) {
					System.out.println(object);
				}
				ret.setLast_measures(MachineDataManager.getInstance().getLastMesures(sensor));
				ObjectMapper mapper= new ObjectMapper();
				try {
					 JsonReader jsonReader = Json.createReader(new StringReader(mapper.writeValueAsString(ret)));
					 JsonObject sensorJson = jsonReader.readObject();
					 jsonReader.close();
					 resultBuilder.add(sensorJson);
				} catch (JsonProcessingException e) {
					logger.error(e.getMessage());
					logger.error("",e);
				}
			
		}		
		return resultBuilder.build();    
    		
    }
          	 
    /**
     * Ritorna la lista dei sensori richiesti e le rispettive misure
     * @param accessToken
     * @parma List<SensorDTO>
     * @return List<SensorDTO>
     */
    @POST
    @Path("/with_measures")
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    @ApiOperation(
    		value = "Get selected sensors with measures")
	@ApiResponses(value = {
			 @ ApiResponse(code = 204, message = "Successful list of sensors modified!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public JsonArray getSelectedSensorsWithMeasures(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true)
    			@QueryParam("access_token") String accessToken,  
    		@ApiParam(value="Sensors List POJO ", required=true)
    			final  List<SensorDTO> sensorsDTO){
    	logger.info("Invocato path @post /with_measures");
    	RestUtil.checkLoginToken(accessToken);
    	JsonArrayBuilder resultBuilder = Json.createArrayBuilder();
    	//List appo = new ArrayList<String>(sensorIds.);
		for (Sensor sensor : MachineDataManager.getInstance().getSensorList()) {
			SensorDTO ret = new SensorAssembler().assembly(sensor);
			if(sensorsDTO.contains(ret)){				
				ret.setLast_measures(MachineDataManager.getInstance().getLastMesures(sensor));
				ObjectMapper mapper= new ObjectMapper();
				try {
					 JsonReader jsonReader = Json.createReader(new StringReader(mapper.writeValueAsString(ret)));
					 JsonObject sensorJson = jsonReader.readObject();
					 jsonReader.close();
					 resultBuilder.add(sensorJson);
				} catch (JsonProcessingException e) {
					logger.error(e.getMessage());
					logger.error("",e);
				}
			}
			
		}		
		return resultBuilder.build();    
    		
    }
    /**
     * Modifica la lista dei sensori disponibili
     * @param accessToken, sensorsDTO
     * @return 
     */
    @POST
    @Path("/")
    @Consumes("application/json;charset=UTF-8")
    @ApiOperation(
			value = "set sensors list")
	@ApiResponses(value = {
			 @ ApiResponse(code = 204, message = "Successful list of sensors modified!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public void setSensors(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true)
    			@QueryParam("access_token") String accessToken,  
    		@ApiParam(value="Sensors List POJO ", required=true)
    			final  List<SensorDTO> sensorsDTO){
    	logger.info("Invocato path @get /sensor");
    	RestUtil.checkLoginToken(accessToken);
    	MachineDataManager.getInstance().configSensorList(sensorsDTO);  	
    }
    
    /**
     * Ritorna tutti i dati disponibili in memoria per il sensor id specificato come path param
     * @param accessToken, sensorId
     * @return SensorDTO
     */
    @GET
    @Path("/{sensor_id}")
    @Produces("application/json;charset=UTF-8")
    @ApiOperation(
			value = "Get sensor data")
	@ApiResponses(value = {
			 @ ApiResponse(code = 204, message = "Successful sensor data returned!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public JsonObject getSensorById(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true)
    			@QueryParam("access_token") String accessToken, 
    		@ApiParam(value="Sensor id to retrieve", required=true)
    			@PathParam("sensor_id") String sensorId){
    	logger.info("Invocato path @get /sensor/{sensor_id}");
    	RestUtil.checkLoginToken(accessToken);
    	JsonObject result = null;
    	
		for (Sensor sensor : MachineDataManager.getInstance().getSensorList()) {
			if(sensor.getSensorId().equalsIgnoreCase(sensorId)){
				SensorDTO ret = new SensorAssembler().assembly(sensor);
				ret.setLast_measures(MachineDataManager.getInstance().getLastMesures(sensor));
				ObjectMapper mapper= new ObjectMapper();
				try {
					 JsonReader jsonReader = Json.createReader(new StringReader(mapper.writeValueAsString(ret)));
					 result = jsonReader.readObject();
					 jsonReader.close();					
				} catch (JsonProcessingException e) {
					logger.error(e.getMessage());
					logger.error("",e);
				}
				break;
			}
		}		
		return result;    	
    }
}

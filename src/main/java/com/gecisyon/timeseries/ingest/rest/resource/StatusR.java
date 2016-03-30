package com.gecisyon.timeseries.ingest.rest.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import com.gecisyon.timeseries.ingest.rest.RestGenericErrorException;
import com.gecisyon.timeseries.ingest.rest.util.RestUtil;
import com.gecisyon.timeseries.ingest.simulator.MachineDataManager;
import com.gecisyon.timeseries.ingest.simulator.MachineDataManager.PoolStatus;
import com.gecisyon.timeseries.ingest.util.TSConfig;
import com.gecisyon.timeseries.ingest.websocket.ClientSocket;

@Path("/status")
@Api(value="Status API")
public class StatusR {
	private static Logger logger = Logger.getLogger(StatusR.class.getName());

	 /**
     * Ritorna tutte le informazioni dello status del servizio 
     * @param accessToken
     * @return JsonObject
     */
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8")
    @ApiOperation(
			value = "Get status of system")
	@ApiResponses(value = {
			 @ ApiResponse(code = 200, message = "Successful retrieval of status!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public JsonObject getStatusAlarmGenerator(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true) 
    			@QueryParam("access_token") String accessToken) {
    	logger.info("Invocato path /status");
    	RestUtil.checkLoginToken(accessToken);
    	TSConfig tsConf = TSConfig.getInstance();
    	
    	JsonObjectBuilder tsStatus = Json.createObjectBuilder()
    			.add("status", MachineDataManager.getInstance().getStatus().name())
    			.add("ws_url",tsConf.getTS_WS_ADDRESS())
    			.add("client_id", tsConf.getCLIENT_ID())
    			.add("connected", ClientSocket.getInstance().isSocketOpen())
    			.add("access_token", (tsConf.getAUTHORIZATION()!=null)?tsConf.getAUTHORIZATION():"");
    	
    	return tsStatus.build();        
    }
    
    /**
     * Imposta lo stato del ThreadPool 
     * @param accessToken,status
     * @return
     */
    @POST
    @Path("/measures_threads")
    @ApiOperation(
			value = "Set status of pool system")
	@ApiResponses(value = {
			 @ ApiResponse(code = 204, message = "Successful retrieval of status!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public void setMeasuresThreadsPoolStatus(
    		@ApiParam(value="Status del servizio (ACTIVE, PAUSED, INACTIVE).", required=true) 
    			@QueryParam("status") PoolStatus status, 
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true) 
    			@QueryParam("access_token") String accessToken) {
    	logger.info("Invocato path /status/measures_threads");
    	RestUtil.checkLoginToken(accessToken);
    	boolean worked = false;
    	switch(status){
    	case PAUSED:
    		MachineDataManager.getInstance().pause();
    		worked = true;
    		break;
    	case ACTIVE:
    		MachineDataManager.getInstance().resume();
    		worked = true;
    		break;
		case INACTIVE:
			break;
		default:
			break;
    	}    	
    	if(!worked)
    		throw new RestGenericErrorException("Status "+status+" non valido..");
    }
}

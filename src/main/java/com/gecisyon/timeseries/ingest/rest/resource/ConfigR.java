package com.gecisyon.timeseries.ingest.rest.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import com.gecisyon.timeseries.ingest.rest.RestGenericErrorException;
import com.gecisyon.timeseries.ingest.rest.dto.TSConfigDTO;
import com.gecisyon.timeseries.ingest.rest.dto.assembler.TSConfigAssembler;
import com.gecisyon.timeseries.ingest.rest.util.RestUtil;
import com.gecisyon.timeseries.ingest.util.TSConfig;

@Path("/config")
@Api(value="Config API")
public class ConfigR {
	private static Logger logger = Logger.getLogger(ConfigR.class.getName());
	
	 /**
     * Ritorna tutti i parametri di configurazione
     * @param accessToken 
     * @return TSConfigDTO
     */
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8")
    @ApiOperation(
			value = "Get config properties")
	@ApiResponses(value = {
			 @ ApiResponse(code = 200, message = "Successful config return!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public TSConfigDTO getConfigProperties(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true)
    			@QueryParam("access_token") String accessToken){
    	logger.info("Invocato path @get /congif");
    	TSConfig tsConf = TSConfig.getInstance();
    	RestUtil.checkLoginToken(accessToken);
    	return new TSConfigAssembler().assembly(tsConf);    	
    }
    
    /**
     * Modifica i parametri di configurazione
     * @param accessToken configDTO
     * @return 
     */
    @POST
    @Path("/")    
	@Consumes("application/json;charset=UTF-8")
    @ApiOperation(
			value = "Set config properties")
	@ApiResponses(value = {
			 @ ApiResponse(code = 204, message = "Successful config return!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public void setConfigProperties(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true)
    			@QueryParam("access_token") String accessToken, 
    		@ApiParam(value="Config POJO ", required=true)
    			final TSConfigDTO configDTO){
    	logger.info("Invocato path @post /congif");
    	RestUtil.checkLoginToken(accessToken);
    	
		if(!TSConfig.reconfigure(configDTO)){		
			throw new RestGenericErrorException("errore nella configurazione.. parametri non validi");
		}
    }
}

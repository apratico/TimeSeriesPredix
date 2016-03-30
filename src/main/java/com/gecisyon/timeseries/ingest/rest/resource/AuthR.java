package com.gecisyon.timeseries.ingest.rest.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import com.gecisyon.timeseries.ingest.rest.RestGenericErrorException;
import com.gecisyon.timeseries.ingest.util.TSConfig;

@Path("/login")
@Api(value="Login API")
public class AuthR {
	private static Logger logger = Logger.getLogger(AuthR.class.getName());
	
	/**
     * Una volta effettuata la login correttamente ritorna l'access_token da utilizzare per tutte le invocazioni REST esposte 
     * @param accessToken
     * @return JsonObject
     */
    @POST
    @Path("/")
    @Produces("application/json;charset=UTF-8")
    @ApiOperation(
			value = "Get access_token by login")
	@ApiResponses(value = {
			 @ ApiResponse(code = 200, message = "Successful access_token generated!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public JsonObject login(
    		@ApiParam(value="client_id of UAA Predix.io Service", required=true) 
    			@QueryParam("client_id") String clientId,
    			@ApiParam(value="client_secret of UAA Predix.io Service", required=true) 
    			@QueryParam("client_secret") String clientSecret) {    	
    	logger.info("Invocato path /login");
    	TSConfig tsConf = TSConfig.getInstance();
    	try {
	    	if(clientId!=null && !clientId.trim().isEmpty() && clientSecret!=null && !clientSecret.trim().isEmpty()){
	    		if(clientId.equals(tsConf.getCLIENT_ID())&& clientSecret.equals(tsConf.getCLIENT_SECRET())){	    				
	    			JsonObjectBuilder jBuild = Json.createObjectBuilder()
	    	    			.add("access_token", tsConf.getBase64Authorization());
	    	    	return jBuild.build();
	    		}else{
	        		throw new RestGenericErrorException("Credenziali non valide..");
	        	}
	    			
	    	}else{
	    		throw new RestGenericErrorException("Credenziali nulle o incomplete..");
	    	}
    	} catch (Exception e) {			
    		throw new RestGenericErrorException(e.getMessage());
		}        
    }
}

package com.gecisyon.timeseries.ingest.rest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestGenericErrorException extends WebApplicationException {
	private static final long serialVersionUID = -7140354111388669964L;
	
	public RestGenericErrorException(String message){
		super(Response.status(Response.Status.BAD_REQUEST).entity(getEntity(message)).type(MediaType.APPLICATION_JSON_TYPE).build());
	}

	private static JsonObject getEntity(String message){
		JsonObject obj = Json.createObjectBuilder()
							.add("message", message)
							.build();		
		return obj;
	}
}

package com.gecisyon.timeseries.ingest.websocket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;

import com.gecisyon.timeseries.ingest.util.TSConfig;
public class ClientConfigurator extends ClientEndpointConfig.Configurator {
    
	static volatile boolean called = false;

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        called = true;
        TSConfig tsconf = TSConfig.getInstance();
        headers.put("Authorization", Arrays.asList(tsconf.getAUTHORIZATION()));
        headers.put("Predix-Zone-Id", Arrays.asList(tsconf.getPREDIX_ZONE_ID()));
        headers.put("origin",Arrays.asList(tsconf.getORIGIN()));
        System.out.println(headers);
    }


}

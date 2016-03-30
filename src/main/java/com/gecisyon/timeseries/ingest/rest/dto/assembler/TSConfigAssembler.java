package com.gecisyon.timeseries.ingest.rest.dto.assembler;

import com.gecisyon.timeseries.ingest.rest.dto.TSConfigDTO;
import com.gecisyon.timeseries.ingest.util.TSConfig;

public class TSConfigAssembler {
	
	public TSConfigDTO assembly(TSConfig tsconfig){
		TSConfigDTO tsconfigDTO = new TSConfigDTO();
		if(tsconfig==null)
			return tsconfigDTO;
		
		tsconfigDTO.setAuthorization(tsconfig.getAUTHORIZATION());
		tsconfigDTO.setClient_id(tsconfig.getCLIENT_ID());
		tsconfigDTO.setClient_secret(tsconfig.getCLIENT_SECRET());
		tsconfigDTO.setOrigin(tsconfig.getORIGIN());
		tsconfigDTO.setPredix_zone_id(tsconfig.getPREDIX_ZONE_ID());
		tsconfigDTO.setTs_ws_address(tsconfig.getTS_WS_ADDRESS());
		tsconfigDTO.setUaa_oauth_token_address(tsconfig.getUAA_OAUTH_TOKEN_ADDRESS());
		tsconfigDTO.setUaa_password(tsconfig.getUAA_PASSWORD());
		tsconfigDTO.setUaa_username(tsconfig.getUAA_USERNAME());
		tsconfigDTO.setLast_measures_to_show(tsconfig.getLAST_MEASURES_TO_SHOW());
		return tsconfigDTO;
	}
	

}

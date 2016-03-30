package com.gecisyon.timeseries.ingest.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Builder
public class TSConfigDTO {

	private String ts_ws_address;
	private String origin;
	private String predix_zone_id;
	private String authorization;
	private String client_id;
	private String client_secret;
	private String uaa_username;
	private String uaa_password;
	private String uaa_oauth_token_address;
	private String grant_type;
	private int last_measures_to_show;
}

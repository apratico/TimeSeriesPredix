app.factory('ConfigurationParams', function($rootScope) {
	
	//PARAMETERS CONFIG
	var config = {
			serverPath    : "/webapi",
//			serverPath    : "http://tsingestion.eu-west-1.elasticbeanstalk.com",
			client_id     : "client_boas_3",
			client_secret : "admin_boas_secret"	
	};
	
	
	return {
		get: function(label) {
			return config[label];
		}
	}
});

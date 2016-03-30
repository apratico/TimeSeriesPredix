app.factory('ApiService', function($resource, ConfigurationParams) {
	var self = {};
	
	var login = $resource( ConfigurationParams.get('serverPath') + '/login', {client_id: '@client_id', client_secret: '@client_secret'}, {query: {method: 'POST', isArray: false}} );
	var status = $resource( ConfigurationParams.get('serverPath') + '/status', {access_token: '@accessToken'}, {query: {method: 'GET', isArray: false}} );
	var config = $resource( ConfigurationParams.get('serverPath') + '/config', {access_token: '@accessToken', json:'@json'}, {
		query: {method: 'GET', isArray: false},
		update: {method: 'POST', isArray: false},
	});
	var sensors = $resource( ConfigurationParams.get('serverPath') + '/sensors', {access_token: '@accessToken'}, {
		query: {method: 'GET', isArray: true},
		update: {method: 'POST', isArray: true},
	});
	var sensorData = $resource( ConfigurationParams.get('serverPath') + '/sensors/with_measures', {access_token: '@accessToken', sensorIdList: '@sensorIdList'}, {query: {method: 'POST', isArray: true}} );
	var statusChange = $resource( ConfigurationParams.get('serverPath') + '/status/measures_threads', {access_token: '@accessToken', status: '@status'}, {update: {method: 'POST', isArray: false}} );
	var alarm = $resource( ConfigurationParams.get('serverPath') + '/alarm/:alarmType', {access_token: '@accessToken', alarmType: '@alarmType'}, {launch: {method: 'GET', isArray: false}} );
	var alarmMap = $resource( ConfigurationParams.get('serverPath') + '/alarm', {access_token: '@accessToken'}, {query: {method: 'GET', isArray: false}} );
	
	self.login = function(client_id, client_secret, callback, errorCallback) {
		login.query(
			{client_id : client_id, client_secret : client_secret},
			function(data) {
	            callback(data);	
			}, 
			function(error) {
				errorCallback(error);
			}
		);
	};
	
	self.getStatus = function(accessToken, callback, errorCallback) {
		status.query(
			{accessToken : accessToken},
			function(data) {
	            callback(data);	
			}, 
			function(error) {
				errorCallback(error);
			}
		);
	};
	
	self.getConfig = function(accessToken, callback, errorCallback) {
		config.query(
			{accessToken : accessToken},
			function(data) {
	            callback(data);	
			}, 
			function(error) {
				errorCallback(error);
			}
		);
	};
	
	self.setConfig = function(accessToken, json, callback, errorCallback) {
		config.update(
			{accessToken : accessToken},
			 json,
			function(data) {
	            callback(data);	
			}, 
			function(error) {
				errorCallback(error);
			}
		);
	};
	
	self.getSensorList = function(accessToken, callback, errorCallback) {
		sensors.query(
				{accessToken : accessToken},
				function(data) {
		            callback(data);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.getSensorDataList = function(accessToken, sensorList, callback, errorCallback) {
		sensorData.query(
				{accessToken : accessToken},
				sensorList,
				function(sensorData) {
		            callback(sensorData);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.saveSensorData = function(accessToken, sensorList, callback, errorCallback) {
		sensors.update(
				{accessToken : accessToken},
				sensorList,
				function(response) {
		            callback(response);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.statusChange = function(accessToken, status, callback, errorCallback) {
		statusChange.update(
				{accessToken : accessToken,
			    status : status},
				function(response) {
		            callback(response);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.alarmLaunch = function(accessToken, alarmType, callback, errorCallback){
		alarm.launch(
				{accessToken : accessToken,
			    alarmType : alarmType},
				function(response) {
		            callback(response);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.getAlarmMap = function(accessToken, callback, errorCallback){
		alarmMap.query(
				{accessToken : accessToken},
				function(response) {
		            callback(response);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
		
	return self;
});



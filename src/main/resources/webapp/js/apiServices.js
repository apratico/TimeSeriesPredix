app.factory('ApiService', function($resource, ConfigurationParams) {
	var self = {};
	
	var login = $resource( ConfigurationParams.get('serverPath') + '/login', {client_id: '@client_id', client_secret: '@client_secret'}, {query: {method: 'POST', isArray: false}} );
	var status = $resource( ConfigurationParams.get('serverPath') + '/status', {access_token: '@access_token'}, {query: {method: 'GET', isArray: false}} );
	var config = $resource( ConfigurationParams.get('serverPath') + '/config', {access_token: '@access_token', json:'@json'}, {
		query: {method: 'GET', isArray: false},
		update: {method: 'POST', isArray: false},
	});
	var sensors = $resource( ConfigurationParams.get('serverPath') + '/sensors', {access_token: '@access_token'}, {
		query: {method: 'GET', isArray: true},
		update: {method: 'POST', isArray: true},
	});
	var sensorData = $resource( ConfigurationParams.get('serverPath') + '/sensors/with_measures', {access_token: '@access_token', sensorIdList: '@sensorIdList'}, {query: {method: 'POST', isArray: true}} );
	var statusChange = $resource( ConfigurationParams.get('serverPath') + '/status/measures_threads', {access_token: '@access_token', status: '@status'}, {update: {method: 'POST', isArray: false}} );
	var alarm = $resource( ConfigurationParams.get('serverPath') + '/alarm/:alarmType', {access_token: '@access_token', alarmType: '@alarmType'}, {launch: {method: 'GET', isArray: false}} );
	var alarmMap = $resource( ConfigurationParams.get('serverPath') + '/alarm', {access_token: '@access_token'}, {query: {method: 'GET', isArray: false}} );
	
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
	
	self.getStatus = function(access_token, callback, errorCallback) {
		status.query(
			{access_token : access_token},
			function(data) {
	            callback(data);	
			}, 
			function(error) {
				errorCallback(error);
			}
		);
	};
	
	self.getConfig = function(access_token, callback, errorCallback) {
		config.query(
			{access_token : access_token},
			function(data) {
	            callback(data);	
			}, 
			function(error) {
				errorCallback(error);
			}
		);
	};
	
	self.setConfig = function(access_token, json, callback, errorCallback) {
		config.update(
			{access_token : access_token},
			 json,
			function(data) {
	            callback(data);	
			}, 
			function(error) {
				errorCallback(error);
			}
		);
	};
	
	self.getSensorList = function(access_token, callback, errorCallback) {
		sensors.query(
				{access_token : access_token},
				function(data) {
		            callback(data);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.getSensorDataList = function(access_token, sensorList, callback, errorCallback) {
		sensorData.query(
				{access_token : access_token},
				sensorList,
				function(sensorData) {
		            callback(sensorData);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.saveSensorData = function(access_token, sensorList, callback, errorCallback) {
		sensors.update(
				{access_token : access_token},
				sensorList,
				function(response) {
		            callback(response);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.statusChange = function(access_token, status, callback, errorCallback) {
		statusChange.update(
				{access_token : access_token,
			    status : status},
				function(response) {
		            callback(response);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.alarmLaunch = function(access_token, alarmType, callback, errorCallback){
		alarm.launch(
				{access_token : access_token,
			    alarmType : alarmType},
				function(response) {
		            callback(response);	
				}, 
				function(error) {
					errorCallback(error);
				}
		);
	};
	
	self.getAlarmMap = function(access_token, callback, errorCallback){
		alarmMap.query(
				{access_token : access_token},
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



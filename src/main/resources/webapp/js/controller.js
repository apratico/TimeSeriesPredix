var app = angular.module('app', ['ngResource', 'yaru22.jsonHuman', 'ui.bootstrap']);

app.controller('AppCtrl', function($rootScope, $scope, ApiService, $http, ConfigurationParams, $uibModal, $timeout){
  var self = this;
	
  self.showLoading = false;
  self.currentModule = null;
  
  //self.client_id = ConfigurationParams.get("client_id");
  //self.client_secret = ConfigurationParams.get("client_secret");
  
  self.setModule = function(moduleName){
	  //funzioni di reset
	  $timeout.cancel(self.sensorReadTimeout);
	  console.log('READ SENSORS: false');
	  self.currentModule = moduleName;
  }
  
  self.login = function(){
	 self.showLoading = true;
	 self.loginStatus = 'Login in progress..';
	 if(self.client_id && self.client_secret){
		 ApiService.login(
				 self.client_id, 
				 self.client_secret,
//				 ConfigurationParams.get("client_id"), 
//				 ConfigurationParams.get("client_secret"),
				 function(response){
					 self.accessToken = response.access_token;
					 window.localStorage.setItem("ts_access_token", response.access_token);
					 self.showLoading = false;
					 
					 self.openStatus();
				 },
				 function(err){
					 self.loginStatus = 'Warning! Incorrect login credentials';
					 self.showLoading = false;
				 }
		);
	 }else{
		 self.loginStatus = 'Please insert your credentials';
		 self.showLoading = false;
	 }
	 
  };
  
  self.logout = function(){
	  self.accessToken = null;
	  window.localStorage.removeItem("ts_access_token");
	  self.setModule(null);
  };
  
  self.openStatusModal = function () {

	    var modalInstance = $uibModal.open({
	      animation: true,
	      templateUrl: 'partials/modalStatusChange.html',
	      controller: 'ModalStatusChangeCtrl',
	      resolve: {
	        status: function () {
	          return self.status.status;
	        }
	      }
	    });

	    modalInstance.result.then(function (newStatus) {
	    	self.showLoading = true;
	    	ApiService.statusChange(
					 self.accessToken,
					 newStatus,
					 function(){
						 self.showLoading = false;
						 self.getStatus(
								  function(status){
									  self.status = status;
								  }
						  );
					 },
					 function(err){
						 alert('ERROR change status : '+err.message);
						 self.showLoading = false;
					 }
			);
	    	
	    });
  };
  
  //STATUS MODULE
  self.openStatus = function(){
	  self.setModule("status");
	  self.getStatus(
			  function(status){
				  self.status = status;
			  }
	  );
  };
  
  self.getStatus = function(callback){
	  self.showLoading = true;
		 ApiService.getStatus(
				 self.accessToken,
				 function(status){
					 self.showLoading = false;
					 callback(status);
				 },
				 function(err){
					 alert('ERROR status : '+err.message);
					 self.showLoading = false;
				 }
	  );
  };
  
  self.getConfig = function(){
	     self.showLoading = true;
		 ApiService.getConfig(
				 self.accessToken,
				 function(config){
					 self.config = config;
					 self.showLoading = false;
				 },
				 function(err){
					 alert('ERROR config : '+err.message);
					 self.showLoading = false;
				 }
		);
  };
		 
  self.setConfig = function(){
	  self.showLoading = true;
		 ApiService.setConfig(
				 self.accessToken,
				 self.config,
				 function(config){
					 alert('Data saved successfully');
					 self.showLoading = false;
				 },
				 function(err){
					 alert('ERROR SALVATAGGIO config : '+err.message);
					 self.showLoading = false;
				 }
	  );
  };
  
  //SENSORS MODULE
  self.openSensorList = function(){
	  self.setModule("sensors");
	  self.getSensorList(
			  function(sensorList){
				  self.sensorList = sensorList;
			  }
	  );
  };
  
  self.getSensorList = function(callback){
	     self.showLoading = true;
	     if(!self.sensorList){
	    	 ApiService.getSensorList(
					 self.accessToken,
					 function(sensorList){
						 self.showLoading = false;
						 callback(sensorList);
					 },
					 function(err){
						 alert('ERROR retrive sensors : '+err.message);
						 self.showLoading = false;
					 }
			);
	     }else{
	    	 self.showLoading = false;
	    	 callback(self.sensorList);
	     }
		 
  };
  
  self.editSensor = function(){
	  self.sensorSelectedTempCopy = angular.copy(self.sensorSelected);
  };
  
  self.newSensor = function(){
	  if(self.sensorList){
		  var emptySensor = angular.copy(self.sensorList[0]);
		  angular.forEach(emptySensor, function(attr, value) {
			  emptySensor[value] = null;
		  });
		  self.sensorSelected = emptySensor;
		  self.sensorSelectedTempCopy = emptySensor;
		  self.sensorList.unshift(emptySensor);
	  }
  };
  
  self.cloneSensor = function(sensor){
	  var clonedSensor = angular.copy(sensor);
	  self.sensorSelected = clonedSensor;
	  self.sensorSelectedTempCopy = clonedSensor;
	  self.sensorList.unshift(clonedSensor);
  };
  
  self.deleteSensor = function(){
	  angular.forEach(self.sensorList, function(sensor, key) {
		  if(self.sensorSelected && sensor.sensor_id == self.sensorSelected.sensor_id){
			  self.sensorList.splice(key, 1);
			  self.sensorSelected = null;
			  self.sensorSelectedTempCopy = null;
		  }
	  });
	  
	  self.commitSensorData();
  };
  
  self.saveSensorData = function(){
	  angular.forEach(self.sensorList, function(sensor, key) {
		  if(sensor.sensor_id == self.sensorSelectedTempCopy.sensor_id){
			  self.sensorList[key] = self.sensorSelectedTempCopy;
		  }
	  });
	  self.sensorSelected = self.sensorSelectedTempCopy; 
	  self.sensorSelectedTempCopy = null;
	  
	  self.commitSensorData();
  };
  
  /** commit delle modifiche effettuate */
  self.commitSensorData = function(){
	  //salvataggio dati
	  ApiService.saveSensorData(
			 self.accessToken,
			 self.sensorList,
			 function(){
				 console.log("SAVING DONE! :)")
			 },
			 function(err){
				 alert('an ERROR occurred saving sensor data : '+err.message);
				 self.openSensorList();
			 }
	 );  
  };
  
  //SENSORS DATA MODULE
  self.openSensorData = function(){
	  self.setModule("sensorData");
	  self.getSensorList(
			  function(sensorList){
				  self.sensorList = sensorList;
				   if($scope.selection && $scope.selection.length>0){
					   self.getSensorDataList();
				   }
			  }
	  );
  };
  
  self.getSensorDataList = function(){
	  
	  self.showLoading = true;
	  ApiService.getSensorDataList(
				 self.accessToken,
				 $scope.selection,
				 function(sensorDataList){
					 self.sensorDataList = sensorDataList;
					 self.showLoading = false;
					 
					 console.log('READ SENSORS: true');
					 $timeout.cancel(self.sensorReadTimeout);
					 self.sensorReadTimeout = $timeout(function() {
						 	self.getSensorDataList();
		    		        console.log('REFRESH SENSOR DATA..')
		    		 }, 10000);
				 },
				 function(err){
					 alert('ERROR retrive sensor data list : '+err.message);
					 self.showLoading = false;
				 }
		);
  };

  $scope.selection = [];
  self.toggleSensorsSelection = function toggleSelection(fruitName) {
	    var idx = $scope.selection.indexOf(fruitName);
	
	    // is currently selected
	    if (idx > -1) {
	      $scope.selection.splice(idx, 1);
	    }else{
	      $scope.selection.push(fruitName);
	    }
	    
	    self.getSensorDataList();
    
  };
  
  self.openAlarmModal = function () {
	  	
	   ApiService.getAlarmMap(
				 self.accessToken,
				 function(alarmMap){
					 var modalInstance = $uibModal.open({
					      animation: true,
					      templateUrl: 'partials/modalAlarm.html',
					      controller: 'AlarmCtrl',
					      resolve: {
					    	  alarmMap: function () {
					          return alarmMap;
					        }
					      }
					    });
	
					    modalInstance.result.then(function (alarmType) {
					    	self.showLoading = true;
					    	ApiService.alarmLaunch(
									 self.accessToken,
									 alarmType,
									 function(){
										 self.showLoading = false;
									 },
									 function(err){
										 alert('ERROR alarm launch : '+err.message);
										 self.showLoading = false;
									 }
							);
					    	
					    });
				 },
				 function(err){
					 alert('ERROR alarm launch : '+err.message);
					 self.showLoading = false;
				 }
		);
	  
};
  
  //INIT APP...
  if(window.localStorage.getItem("ts_access_token")){
	  self.accessToken = window.localStorage.getItem("ts_access_token");
	  self.openStatus();
  }
});


app.controller('ModalStatusChangeCtrl', function($scope, $uibModalInstance, status){
	
	$scope.status = status;
	
	if(status=='PAUSED' || status=='INACTIVE'){
		$scope.btnOkLabel = "Start";
		$scope.newStatus = "ACTIVE";
		$scope.btnType = 'btn-success';
	}
	
	if(status=='ACTIVE'){
		$scope.btnOkLabel = "Stop";
		$scope.newStatus = "PAUSED";
		$scope.btnType = 'btn-danger';
	}
	
	$scope.ok = function () {
		$uibModalInstance.close($scope.newStatus);
	};

	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
});

app.controller('ModalAlarmCtrl', function($scope, $uibModalInstance, status){
	
	$scope.status = status;
	
	if(status=='PAUSED' || status=='INACTIVE'){
		$scope.btnOkLabel = "Start";
		$scope.newStatus = "ACTIVE";
		$scope.btnType = 'btn-success';
	}
	
	if(status=='ACTIVE'){
		$scope.btnOkLabel = "Stop";
		$scope.newStatus = "PAUSED";
		$scope.btnType = 'btn-danger';
	}
	
	$scope.ok = function () {
		$uibModalInstance.close($scope.newStatus);
	};

	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
});

app.controller('AlarmCtrl', function($scope, $uibModalInstance, alarmMap){
	
	$scope.alarmMap = alarmMap;
	
	$scope.ok = function () {
		$uibModalInstance.close($scope.alarmType);
	};

	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
});
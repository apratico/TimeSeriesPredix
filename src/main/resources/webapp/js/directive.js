app.directive('formJson', function(){
    return {
        restrict: 'E',
        templateUrl: 'partials/formJson.html',
        scope: {
        	json : '=',
        	dataSubmitFn : '='
        },
        link: function(scope, element, attrs) {
        	scope.submit = function(){
        		scope.$evalAsync(attrs.submitFn);
        	}
        },
        controller: function ($scope, $element, $attrs) {
        	
        },
    };
});

app.directive('sensorGraph', function($timeout){
	return {
		restrict: 'E',
        templateUrl: 'partials/sensorGraph.html',
        scope: {
        	sensorDataList : '='
        },
        link: function(scope, element, attrs) {
        	scope.seriesOptions = [];
        	
        	//INIT GRAPH
        	scope.$watch('sensorDataList', function(value){
        		scope.init();	
        	});
        	
        },
        controller: function ($scope, $element, $attrs) {
        	$scope.init = function(){
        		$scope.seriesOptions = [];
        		angular.forEach($scope.sensorDataList, function(sensor, key) {
        			$scope.seriesOptions.push({
                        name: sensor.sensor_ds,
                        data: sensor.last_measures
                    });
        		});
        		
        		$scope.createChart();
        	};
        	
        	$scope.createChart = function() {
        		$element.highcharts('StockChart', {
        			chart: {
        	            width: 900,
        	            height: 600
        	        },
                    yAxis: {
                        labels: {
                            formatter: function () {
                                return (this.value > 0 ? ' + ' : '') + this.value + '%';
                            }
                        },
                        plotLines: [{
                            value: 0,
                            width: 2,
                            color: 'silver'
                        }]
                    },

                    plotOptions: {
                        series: {
                            compare: 'percent'
                        }
                    },

                    tooltip: {
                        pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.change}%)<br/>',
                        valueDecimals: 2
                    },

                    series: $scope.seriesOptions
                });
            };
        },
	};
});

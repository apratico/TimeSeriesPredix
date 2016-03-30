package com.gecisyon.timeseries.ingest.rest.resource;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.gecisyon.timeseries.ingest.rest.dto.SensorDTO;
import com.gecisyon.timeseries.ingest.rest.dto.assembler.SensorAssembler;
import com.gecisyon.timeseries.ingest.rest.util.RestUtil;
import com.gecisyon.timeseries.ingest.simulator.model.Sensor;
 
@Path("/dev")
@Api(value="Developer API")
public class EntryPoint {
	private static Logger logger = Logger.getLogger(EntryPoint.class.getName());
	
	
//	@GET
//    @Path("/")
//    @Produces(MediaType.TEXT_HTML)
//    public String getApiRif() {
//        return "Test";
//    }
	
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
			value = "Test API")
	@ApiResponses(value = {
			 @ ApiResponse(code = 200, message = "Successful tested!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public String test() {
        return "Test";
    }
    
    /* ************************************************************************************************************ */
    /*							           F O R     D E V    S C O P E												*/
    /* ************************************************************************************************************ */
    @GET
    @Path("/generate_sensors")
    @Produces("application/json;charset=UTF-8")    
    @ApiOperation(
			value = "Dev - API")
	@ApiResponses(value = {
			 @ ApiResponse(code = 200, message = "Successful!"),
			 @ ApiResponse(code = 400, message = "Bad request. Please verify error message, detail and code inside the returned json."),
			 @ ApiResponse(code = 500, message = "Internal server error")			 
		})
    public List<SensorDTO> generateSensors(
    		@ApiParam(value="Authentication token provided by Base64(client_id:client_secret).", required=true)
    			@QueryParam("access_token") String accessToken){
    	logger.info("Invocato path @get /sensor");
    	RestUtil.checkLoginToken(accessToken);
    	List<Sensor> sensorList = new ArrayList<Sensor>();
    	generateDataMock(sensorList);
    	return new SensorAssembler().assembly(sensorList);    	
    }     
    private List<Sensor> generateDataMock(List<Sensor> sensorList ){
    	//ASSET_1	COMPONENT_01	Generator	SENSOR_01	Gearbox Vibration	Vibrazione	g (accellerazione)	-100	-15	1	15	100
    	Sensor sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_01")
    			.componentDs("Generator")
    			.sensorId("SENSOR_01")
    			.sensorDs("Gearbox Vibration")
    			.oum("g")
    			.rangeMin(-100)
    			.alertMin(-15)
    			.avg(1)
    			.alertMax(15)
    			.rangeMax(100)
    			.sample(10000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
    	//ASSET_1	COMPONENT_01	Generator	SENSOR_02	Power Output	Potenza generata	kW	0	30	50	60	60	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_01")
    			.componentDs("Generator")
    			.sensorId("SENSOR_02")
    			.sensorDs("Power Output")
    			.oum("kw")
    			.rangeMin(0)
    			.alertMin(30)
    			.avg(50)
    			.alertMax(60)
    			.rangeMax(80)
    			.sample(10000l)
    			.alarmType(2)
    			.alarmMeasures(5)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_01	Generator	SENSOR_03	RPM	Giri al minuto	RPM	0	5	20	30	30	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_01")
    			.componentDs("Generator")
    			.sensorId("SENSOR_03")
    			.sensorDs("RPM")
    			.oum("rpm")
    			.rangeMin(0)
    			.alertMin(5)
    			.avg(20)
    			.alertMax(30)
    			.rangeMax(45)
    			.sample(10000l)
    			.alarmType(2)
    			.alarmMeasures(5)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_01	Generator	SENSOR_04	Voltage	Volts generati	V (volt)	0	200	220	220	230	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_01")
    			.componentDs("Generator")
    			.sensorId("SENSOR_04")
    			.sensorDs("Voltage")
    			.oum("rpm")
    			.rangeMin(0)
    			.alertMin(200)
    			.avg(220)
    			.alertMax(220)
    			.rangeMax(230)
    			.sample(10000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_01	Generator	SENSOR_05	Air cooling RPM	Giri al minuto	RPM	0	300	450	550	600	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_01")
    			.componentDs("Generator")
    			.sensorId("SENSOR_05")
    			.sensorDs("Air cooling RPM")
    			.oum("rpm")
    			.rangeMin(0)
    			.alertMin(300)
    			.avg(450)
    			.alertMax(550)
    			.rangeMax(600)
    			.sample(10000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_01	Generator	SENSOR_06	Temperature	Temperatura	° (celsius)	0	30	70	85	120	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_01")
    			.componentDs("Generator")
    			.sensorId("SENSOR_06")
    			.sensorDs("Temperature")
    			.oum("celsius")
    			.rangeMin(0)
    			.alertMin(30)
    			.avg(70)
    			.alertMax(85)
    			.rangeMax(120)
    			.sample(10000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_02	Rotor	SENSOR_07	Pitch angle	Angolo di inclinazione pala	° (degree)	0	15	15	25	30	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_01")
    			.componentDs("Rotor")
    			.sensorId("SENSOR_07")
    			.sensorDs("Temperature")
    			.oum("degree")
    			.rangeMin(0)
    			.alertMin(15)
    			.avg(15)
    			.alertMax(25)
    			.rangeMax(30)
    			.sample(10000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_02	Rotor	SENSOR_08	Vibration	Vibrazione	g (accellerazione)	-100	-20	1	20	100	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_01")
    			.componentDs("Rotor")
    			.sensorId("SENSOR_08")
    			.sensorDs("Vibration")
    			.oum("g")
    			.rangeMin(-100)
    			.alertMin(-20)
    			.avg(1)
    			.alertMax(20)
    			.rangeMax(100)
    			.sample(10000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//		ASSET_1	COMPONENT_02	Rotor	SENSOR_09	Pressure	Sensore di pressione	psi (pound per square inch)	0	5	15	40	50	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_02")
    			.componentDs("Rotor")
    			.sensorId("SENSOR_09")
    			.sensorDs("Pressure")
    			.oum("psi")
    			.rangeMin(0)
    			.alertMin(5)
    			.avg(15)
    			.alertMax(40)
    			.rangeMax(50)
    			.sample(10000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_02	Rotor	SENSOR_10	Absolute blade pitch	Inclinazione pale	° (degree)	10	15	15	25	30	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_02")
    			.componentDs("Rotor")
    			.sensorId("SENSOR_10")
    			.sensorDs("Absolute blade pitch")
    			.oum("degree")
    			.rangeMin(10)
    			.alertMin(15)
    			.avg(15)
    			.alertMax(25)
    			.rangeMax(30)
    			.sample(15000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_02	Rotor	SENSOR_11	Oil Level	Livello olio	%	0	50	80	99	100	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_02")
    			.componentDs("Rotor")
    			.sensorId("SENSOR_11")
    			.sensorDs("Oil Level")
    			.oum("percentage")
    			.rangeMin(0)
    			.alertMin(50)
    			.avg(80)
    			.alertMax(99)
    			.rangeMax(100)
    			.sample(15000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_02	Rotor	SENSOR_12	Oli Temp	Temperatura Olio	° (celsius)	-10	20	70	80	100	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_02")
    			.componentDs("Rotor")
    			.sensorId("SENSOR_12")
    			.sensorDs("Oil Temp")
    			.oum("celsius")
    			.rangeMin(-10)
    			.alertMin(20)
    			.avg(70)
    			.alertMax(80)
    			.rangeMax(100)
    			.sample(15000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_03	Weather Station	SENSOR_13	Speed	Velocità del vento	kt (nodi)	0	2	15	50	80	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_03")
    			.componentDs("Weather Station")
    			.sensorId("SENSOR_13")
    			.sensorDs("Speed")
    			.oum("kt")
    			.rangeMin(0)
    			.alertMin(2)
    			.avg(15)
    			.alertMax(50)
    			.rangeMax(80)
    			.sample(15000l)
    			.alarmType(1)
    			.alarmMeasures(5)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_03	Weather Station	SENSOR_14	Direction	Direzione del vento	° (degree)	1	45	180	270	360	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_03")
    			.componentDs("Weather Station")
    			.sensorId("SENSOR_14")
    			.sensorDs("Direction")
    			.oum("degree")
    			.rangeMin(1)
    			.alertMin(45)
    			.avg(180)
    			.alertMax(270)
    			.rangeMax(360)
    			.sample(15000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_03	Weather Station	SENSOR_15	Humidity	Umidità	%	0	10	75	95	100	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_03")
    			.componentDs("Weather Station")
    			.sensorId("SENSOR_15")
    			.sensorDs("Humidity")
    			.oum("percentage")
    			.rangeMin(0)
    			.alertMin(10)
    			.avg(75)
    			.alertMax(95)
    			.rangeMax(100)
    			.sample(15000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_03	Weather Station	SENSOR_16	Temperature	Temperatura	° (celsius)	-40	10	25	45	80	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_03")
    			.componentDs("Weather Station")
    			.sensorId("SENSOR_16")
    			.sensorDs("Temperature")
    			.oum("celsius")
    			.rangeMin(-40)
    			.alertMin(10)
    			.avg(25)
    			.alertMax(45)
    			.rangeMax(80)
    			.sample(15000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_03	Weather Station	SENSOR_17	Rain	Accumulo pioggia	mm	0	1	1000	3000	5000	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_03")
    			.componentDs("Weather Station")
    			.sensorId("SENSOR_17")
    			.sensorDs("Rain")
    			.oum("mm")
    			.rangeMin(0)
    			.alertMin(1)
    			.avg(1000)
    			.alertMax(3000)
    			.rangeMax(5000)
    			.sample(15000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_03	Weather Station	SENSOR_18	Pressure	Pressione	hPA	990	995	1013	1018	1028	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_03")
    			.componentDs("Weather Station")
    			.sensorId("SENSOR_18")
    			.sensorDs("Pressure")
    			.oum("hpa")
    			.rangeMin(990)
    			.alertMin(995)
    			.avg(1013)
    			.alertMax(1018)
    			.rangeMax(1028)
    			.sample(5000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_04	Yaw mechanism	SENSOR_19	Pressure	Sensore di pressione	psi (pound per square inch)	0	5	15	25	50	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_04")
    			.componentDs("Yaw mechanism")
    			.sensorId("SENSOR_19")
    			.sensorDs("Pressure")
    			.oum("psi")
    			.rangeMin(0)
    			.alertMin(5)
    			.avg(15)
    			.alertMax(25)
    			.rangeMax(50)
    			.sample(5000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_04	Yaw mechanism	SENSOR_20	Yaw angle	Angolo di inclinazione rispetto al vento	° (degree)	-90	-45	0	45	90	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_04")
    			.componentDs("Yaw mechanism")
    			.sensorId("SENSOR_20")
    			.sensorDs("Yaw angle")
    			.oum("degree")
    			.rangeMin(-90)
    			.alertMin(-45)
    			.avg(0)
    			.alertMax(45)
    			.rangeMax(90)
    			.sample(5000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_04	Yaw mechanism	SENSOR_21	Oil Level	Livello olio	%	0	50	80	95	100	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_04")
    			.componentDs("Yaw mechanism")
    			.sensorId("SENSOR_21")
    			.sensorDs("Oil Level")
    			.oum("percentage")
    			.rangeMin(0)
    			.alertMin(50)
    			.avg(80)
    			.alertMax(90)
    			.rangeMax(100)
    			.sample(5000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_04	Yaw mechanism	SENSOR_22	Yaw motor kW	Potenza motore yaw	kW	1	2	3	4	5	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_04")
    			.componentDs("Yaw mechanism")
    			.sensorId("SENSOR_22")
    			.sensorDs("Yaw motor")
    			.oum("kw")
    			.rangeMin(1)
    			.alertMin(2)
    			.avg(3)
    			.alertMax(4)
    			.rangeMax(5)
    			.sample(5000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_04	Yaw mechanism	SENSOR_23	Cable Twist Counter	Conteggio giri cavi	number	0	1	20	100	200	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_04")
    			.componentDs("Yaw mechanism")
    			.sensorId("SENSOR_23")
    			.sensorDs("Cable Twist Counter")
    			.oum("number")
    			.rangeMin(0)
    			.alertMin(1)
    			.avg(20)
    			.alertMax(100)
    			.rangeMax(200)
    			.sample(5000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
//    	ASSET_1	COMPONENT_04	Yaw mechanism	SENSOR_24	Oil Level	Livello olio	%	0	50	80	95	100	15 secondi
    	sensor = Sensor.builder()
    			.assetId("ASSET_1")
    			.componentId("COMPONENT_04")
    			.componentDs("Yaw mechanism")
    			.sensorId("SENSOR_24")
    			.sensorDs("Oil Level")
    			.oum("percentage")
    			.rangeMin(0)
    			.alertMin(501)
    			.avg(80)
    			.alertMax(95)
    			.rangeMax(100)
    			.sample(5000l)
    			.alarmType(0)
    			.alarmMeasures(0)
    			.build();
    	sensorList.add(sensor);
    	return sensorList;
    }
    
   
    
    
}
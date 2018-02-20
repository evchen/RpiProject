package com.ericsson.appiot.examples.gateway.rpigateway;

import com.ericsson.appiot.gateway.AppIoTGateway;
import com.ericsson.appiot.gateway.BaseAppIoTListener;
import com.ericsson.appiot.gateway.GatewayException;
import com.ericsson.appiot.gateway.InitializationException;
import com.ericsson.appiot.gateway.deviceregistry.DeviceRegistration;
import com.ericsson.appiot.gateway.dto.SettingCategory;
import com.ericsson.appiot.gateway.dto.DeviceDiscoveryRequest;
import com.ericsson.appiot.gateway.dto.DeviceRegisterRequest;
import com.ericsson.appiot.gateway.dto.ResourceLink;
import com.ericsson.appiot.gateway.dto.ResponseCode;
import com.ericsson.appiot.gateway.senml.SenMlObject;
import com.ericsson.appiot.gateway.senml.SenMlEntry;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// MyAppIotListener writes debug message for all requests but for read and observe request

public class MyAppIoTListener extends BaseAppIoTListener {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private final String TEMPERATURE = "/3303/0/5700";
	private final String HUMIDITY = "/3304/0/5700";
	private List<String> periodicDataUrl;

	String endpoint = "g4rag3"; // a field that should be synchonized with AppIoT

	@Override
	public void init(AppIoTGateway gateway) throws InitializationException{
		super.init(gateway);

		periodicDataUrl = new LinkedList<String>();

		periodicDataUrl.add(TEMPERATURE);
		periodicDataUrl.add(HUMIDITY);
		
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run(){
				periodicDataCollect();
			}

		}, 10, 0, TimeUnit.SECONDS);
	}

	private void periodicDataCollect(){
		
		SenMlObject object = new SenMlObject();
		object.setBaseName(endpoint);
		object.setBaseTime(System.currentTimeMillis() / 1000);
		
		periodicDataUrl.forEach(url ->{
			RpiResource resource = new RpiResource();	
			object.addEntry(resource.getContent(url));
			}
			);

		try{
			getGateway().sendResourceObservationValue(object);
		}
		catch(GatewayException e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}			

		logger.log(Level.INFO, "Periodically report back to AppIoT");

	}

	@Override
	public void onCustomCommandRequest(String correlationId, String actorId, String payloadType, String payload){

		logger.log(Level.INFO, String.format("RECEIVED CUSTOM COMMAND REQUEST"));

		logger.log(Level.INFO, String.format("Correlation ID:  %s", correlationId));
		logger.log(Level.INFO, String.format("Actor ID:  %s", actorId));

		logger.log(Level.INFO, String.format("Payload Type:  %s", payloadType));
		logger.log(Level.INFO, String.format("Payload Message:  %s", payload));
	}


	@Override
	public void onDeviceDiscoveryRequest(String correlationId, DeviceDiscoveryRequest deviceDiscoveryRequest){

		logger.log(Level.INFO, String.format("RECEIVED DEVICE DISCOVERY REQUEST"));

		logger.log(Level.INFO, String.format("Correlation ID:  %s", correlationId));
		logger.log(Level.INFO, String.format("Request message: %s", deviceDiscoveryRequest));
	}

	@Override
	public void onDeviceRegisterRequest(String correlationId, String endpoint, DeviceRegisterRequest deviceRegisterRequest){

		logger.log(Level.INFO, String.format("RECEIVED DEVICE REGISTER REQUEST"));

		logger.log(Level.INFO, String.format("Correlation ID:  %s", correlationId));
		logger.log(Level.INFO, String.format("End point:  %s", endpoint));


		logger.log(Level.INFO, String.format("Request message:  %s", deviceRegisterRequest));
	}

	@Override
	public void onGatewayUpdateSettingsRequest(List<SettingCategory> settingCategories){

		logger.log(Level.INFO, String.format("RECEIVED GATEWAY UPDATE SETTING REQUEST"));

		settingCategories.forEach(settingCategory -> 
				logger.log(Level.INFO, String.format("Setting Name:  %s  with value:  %s",settingCategory.getName(), settingCategory.getSettings())
					));
	}


	@Override
	public void onResourceCancelObserveRequest(String correlationId, String endpoint, ResourceLink resourceLink){

		logger.log(Level.INFO, String.format("RECEIVED RESOURCE CANCEL OBSERVE REQUEST"));

		logger.log(Level.INFO, String.format("Correlation ID:  %s", correlationId));
		logger.log(Level.INFO, String.format("End point:  %s", endpoint));

		logger.log(Level.INFO, String.format("Cancel Message:%nURL:  %s%nValue:  %s", resourceLink.getUrl(), resourceLink.getValue()));
	}


	@Override
	public void onResourceReadRequest(String correlationId, String endpoint, ResourceLink resourceLink){

		logger.log(Level.INFO, String.format("RECEIVED RESOURCE READ REQUEST"));

		logger.log(Level.INFO, String.format("Correlation ID:  %s", correlationId));
		logger.log(Level.INFO, String.format("End point:  %s", endpoint));

		logger.log(Level.INFO, String.format("Cancel Message:%nURL:  %s%nValue:  %s", resourceLink.getUrl(), resourceLink.getValue()));

		try{
			if( resourceLink.getUrl().equals(TEMPERATURE) || resourceLink.getUrl().equals(HUMIDITY) )  getGateway().sendResourceReadResponse(correlationId, endpoint, new ResponseCode(ResponseCode.VALID));
			else {
				getGateway().sendResourceReadResponse(correlationId, endpoint, new ResponseCode(ResponseCode.NOT_IMPLEMENTED));
				return;
			}
			logger.log(Level.INFO, "Resource read request answered with valid");
		}
		catch (GatewayException e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}

		try{
			SenMlObject object = new SenMlObject();
			object.setBaseName(endpoint);
			object.setBaseTime(System.currentTimeMillis() / 1000);
			RpiResource resource = new RpiResource();	
			object.addEntry(resource.getContent(resourceLink.getUrl()));

			getGateway().sendResourceObservationValue(object);

			logger.log(Level.INFO, "Sent back the value 30 to AppIoT as response");
		}
		catch (GatewayException e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}
}



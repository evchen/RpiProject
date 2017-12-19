package com.ericsson.appiot.examples.gateway.rpigateway;

import com.ericsson.appiot.gateway.AppIoTGateway;
import com.ericsson.appiot.gateway.AppIoTListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RpiGateway {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private AppIoTListener appiotListener;
	private AppIoTGateway appiotGateway;

	public static void main(String[] args) {
		
		RpiGateway gateway = new RpiGateway();
		
		gateway.init();
		
		gateway.start();

	}

	private void init() {
		logger.log(Level.INFO, "RPi Gateway initializing...");
		appiotListener = new MyAppIoTListener();
		appiotGateway = new AppIoTGateway(appiotListener);
	}

	private void start(){
		appiotGateway.start();
	}
}


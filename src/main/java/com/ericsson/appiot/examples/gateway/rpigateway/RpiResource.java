package com.ericsson.appiot.examples.gateway.rpigateway;

import com.ericsson.appiot.gateway.senml.SenMlEntry;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;


public class RpiResource {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	public String COMMAND = "python src/resource/readSensorData.py";

	public SenMlEntry getContent(String url){
		
		SenMlEntry senMlEntry = new SenMlEntry();
		senMlEntry.setName(url);
		senMlEntry.setValue(getValue(url));
		
		return senMlEntry;	

	}

	public Double getValue(String url){
		Double f = new Double(0);
		Runtime r = Runtime.getRuntime();
		try{
			
			Process process = r.exec("ls");
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = br.readLine();
			System.out.println(line);
			
		}
		catch (Exception e){}
		try{
			Process process = r.exec(COMMAND + " " + url);
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = br.readLine();
			System.out.println(line);
			f = Double.parseDouble(line);
		}
		catch(Exception e){
			System.out.println(e);
		}

		return f;
	}
}

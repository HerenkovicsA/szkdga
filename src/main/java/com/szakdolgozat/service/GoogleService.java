package com.szakdolgozat.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleService {
	
	private static HttpsURLConnection con;
	private static final String DISTANCES_MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json?";
	private static final String API_KEY = System.getenv("GOOGLE_API_KEY");

	//https://maps.googleapis.com/maps/api/distancematrix/json?origins=Boston,MA|Charlestown,MA&destinations=Lexington,MA|Concord,MA&departure_time=now&key=YOUR_API_KEY
	
	/**
	 * getDistance
	 * @param askGoogle
	 * @param origin
	 * @param destination
	 * @return if 0 there was a problem with the requests. Otherwise returns the distance in meters.
	 */
	public int getDistance(boolean askGoogle, String origin, String destination) {
		JsonNode jsNode = getDistanceJson(askGoogle, origin, destination);
		int distance = jsNode.findValue("distance").findValue("value").asInt();
		System.out.println("getDistnace "+ distance);
		return distance;
	}
	
	private JsonNode getDistanceJson(boolean askGoogle, String origin, String destination) {

		JsonNode rootNode = null;
	
	    if(askGoogle) {
		    try {
				String from = formAddressForUrl(origin);
				String to = formAddressForUrl(destination);

				String url = DISTANCES_MATRIX_API + "origins=" + from + "&destinations=" + to + "&key=" + API_KEY;
		    	
		        URL myurl = new URL(url);
		        con = (HttpsURLConnection) myurl.openConnection();
		
		        con.setDoOutput(true);
		        con.setRequestMethod("GET");
		
		        StringBuilder content;
		
		        try (BufferedReader in = new BufferedReader(
		                new InputStreamReader(con.getInputStream()))) {
		
		            String line;
		            content = new StringBuilder();
		            
		            while ((line = in.readLine()) != null) {
		                content.append(line);
		                content.append(System.lineSeparator());
		            }
		        }
	
		        rootNode = new ObjectMapper().readTree(content.toString());
		    } catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  finally {
		        
		        con.disconnect();
		    }
	    }else {
		    
		    String content = "{\r\n" + 
		    		"   \"destination_addresses\" : [ \"Dunaszeg, Rákóczi Ferenc u. 8, 9174 Hungary\" ],\r\n" + 
		    		"   \"origin_addresses\" : [ \"Kunsziget, Ifjúság u. 13, 9184 Hungary\" ],\r\n" + 
		    		"   \"rows\" : [\r\n" + 
		    		"      {\r\n" + 
		    		"         \"elements\" : [\r\n" + 
		    		"            {\r\n" + 
		    		"               \"distance\" : {\r\n" + 
		    		"                  \"text\" : \"7.0 km\",\r\n" + 
		    		"                  \"value\" : 6952\r\n" + 
		    		"               },\r\n" + 
		    		"               \"duration\" : {\r\n" + 
		    		"                  \"text\" : \"10 mins\",\r\n" + 
		    		"                  \"value\" : 616\r\n" + 
		    		"               },\r\n" + 
		    		"               \"status\" : \"OK\"\r\n" + 
		    		"            }\r\n" + 
		    		"         ]\r\n" + 
		    		"      }\r\n" + 
		    		"   ],\r\n" + 
		    		"   \"status\" : \"OK\"\r\n" + 
		    		"}"; 
		    try {
				rootNode = new ObjectMapper().readTree(content.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
		return rootNode;

	}

	private String formAddressForUrl(String address) throws UnsupportedEncodingException {
		return URLEncoder.encode(address, "UTF-8");
	}
	
}

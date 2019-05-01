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
public class GoogleServiceImpl implements GoogleService{
	
	private static HttpsURLConnection con;
	private static final String DISTANCES_MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json?";
	private static final String GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?";
	private static final String API_KEY = System.getenv("GOOGLE_API_KEY");
	
	/**
	 * Get distance between two addresses from Google API.
	 * @param origin
	 * @param destination
	 * @return if 0 there was a problem with the requests. Otherwise returns the distance in meters.
	 */
	@Override
	public int getDistance(String origin, String destination) throws Exception{
		JsonNode jsNode = getDistanceJson(origin, destination);
		if(jsNode == null || jsNode.isNull()) throw new Exception("Problem while using distance API");
		int distance = jsNode.findValue("distance").findValue("value").asInt();
		return distance;
	}
	
	@Override
	public boolean validateAddress(String address) {
		JsonNode jsNode = validate(address);
		if(jsNode == null || jsNode.isNull()) {
			return false;
		}
		if(jsNode.findValue("location_type") == null) {
			return false;
		}
		String locationType = jsNode.findValue("location_type").asText();
		return locationType.equalsIgnoreCase("ROOFTOP");
	}
	
	private JsonNode validate(String address) {
		String formedAddress = formAddressForUrl(address);
		String url = GEOCODE_API + "address=" + formedAddress + "&key=" + API_KEY;
    	return doGet(url);
	}

	private JsonNode getDistanceJson(String origin, String destination) {
		String from = formAddressForUrl(origin);
		String to = formAddressForUrl(destination);
		String url = DISTANCES_MATRIX_API + "origins=" + from + "&destinations=" + to + "&key=" + API_KEY;
		return doGet(url);
	}

	private JsonNode doGet(String url) {
		JsonNode rootNode = null;
		try {
			
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
	    
		return rootNode;
	}
	
	private String formAddressForUrl(String address) {
		if(address != null)
			try {
				return URLEncoder.encode(address, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "problem";
			}
		return "problem";
	}
	
}

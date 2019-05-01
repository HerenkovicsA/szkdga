package com.szakdolgozat.service;

public interface GoogleService {
	/**
	 * Get distance between two addresses from Google API.
	 * @param askGoogle
	 * @param origin
	 * @param destination
	 * @return if 0 there was a problem with the requests. Otherwise returns the distance in meters.
	 */
	int getDistance(String origin, String destination) throws Exception;
	
	boolean validateAddress(String address);
}

package com.retail.poc.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retail.poc.model.Coordinates;
import com.retail.poc.services.interfaces.GoogleService;
import com.retail.poc.util.DistanceCalculator;
import com.retail.poc.util.HttpConnectionUtil;

@Service
public class GoogleServiceImpl implements GoogleService {

	@Autowired
	private HttpConnectionUtil httpConnectionUtil;
	@Autowired
	private DistanceCalculator distanceCalculator;

	final static Logger LOGGER = Logger.getLogger(GoogleServiceImpl.class);

	@Override
	public Coordinates getCoordinates(String address) {
		return httpConnectionUtil.getCoordinates(address);
	}

	@Override
	public double distanceInMeters(double lat1, double lon1, double lat2,
			double lon2) {
		return distanceCalculator.distanceInMeters(lat1, lon1, lat2, lon2);
	}

}

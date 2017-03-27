package com.retail.poc.services.interfaces;

import com.retail.poc.model.Coordinates;

public interface GoogleService {
	Coordinates getCoordinates(String address);
	double distanceInMeters(double customerLatitude, double customerLongitude, double shopLatitude, double shopLongitude);
}

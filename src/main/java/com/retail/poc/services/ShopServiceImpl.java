package com.retail.poc.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.retail.poc.exceptions.ShopNotFoundException;
import com.retail.poc.model.Coordinates;
import com.retail.poc.model.Shop;
import com.retail.poc.model.ShopWithCoordinates;
import com.retail.poc.services.interfaces.GoogleService;
import com.retail.poc.services.interfaces.ShopService;

@Service
public class ShopServiceImpl implements ShopService {

	final static Logger LOGGER = Logger.getLogger(ShopServiceImpl.class);

	@Autowired
	private GoogleService googleService;

	private static ConcurrentHashMap<String, ShopWithCoordinates> cache = new ConcurrentHashMap<>();

	public ShopWithCoordinates saveOrUpdate(Shop shop) {
		ShopWithCoordinates shopWithCoordinates = new ShopWithCoordinates();
		Coordinates coordinates = googleService.getCoordinates(generateAddressFromShop(shop));
		shopWithCoordinates.setShopName(shop.getShopName());
		shopWithCoordinates.setShopAddress(shop.getShopAddress());
		shopWithCoordinates.setCoordinates(coordinates);
		cache.put(shop.getShopName(), shopWithCoordinates);
		return shopWithCoordinates;
	}

	public ShopWithCoordinates findClosestShopWithCoordinates(String customerLatitude, String customerLongitude)
			throws ShopNotFoundException {
		
		Map<String, Double> shopsDistanceInMeters = new HashMap<>();

		for (Map.Entry<String, ShopWithCoordinates> cacheEntry : cache.entrySet()) {
			ShopWithCoordinates shopWithCoordinates = cacheEntry.getValue();

			Coordinates shopCoordinates = shopWithCoordinates.getCoordinates();
			
			if (!StringUtils.isEmpty(shopCoordinates.getLatitude())
					&& !StringUtils.isEmpty(shopCoordinates.getLongitude())) {

				double distanceInMeters = googleService
							   .distanceInMeters(getDoubleValue(customerLatitude),
								getDoubleValue(customerLongitude),
								getDoubleValue(shopCoordinates.getLatitude()),
								getDoubleValue(shopCoordinates.getLongitude()));
				
				shopsDistanceInMeters.put(cacheEntry.getKey(),
						distanceInMeters);
			}
		}

		return findNearestShop(shopsDistanceInMeters, customerLatitude, customerLongitude);
		
	}

	private ShopWithCoordinates findNearestShop(
			Map<String, Double> shopsDistanceInMeters, String customerLatitude, String customerLongitude) throws ShopNotFoundException {
		
		ShopWithCoordinates nearestShopWithCoordinates = null;
		Optional<String> nearestShopName = (Optional<String>) shopsDistanceInMeters
				.entrySet().stream()
				.sorted(Comparator.comparingDouble(Map.Entry::getValue))
				.findFirst().map(Map.Entry::getKey);
		if (!nearestShopName.isPresent()) {
			String errorMessage = "No shop found near your location for given coordinates. Latitude:"
					+ customerLatitude + ", Longitude:" + customerLongitude;
			LOGGER.error(errorMessage);
			throw new ShopNotFoundException(errorMessage);
		}

		nearestShopWithCoordinates = cache.get(nearestShopName.get());
		return nearestShopWithCoordinates;
	}

	private double getDoubleValue(String value) {
		return Double.parseDouble(value);
	}

	private String generateAddressFromShop(Shop shop) {
		return new StringBuilder(shop.getShopName()).append("+")
				.append(shop.getShopAddress().getNumber()).append("+")
				.append(shop.getShopAddress().getPostCode()).toString();
	}

	public Object getAllShops() {
		return new ArrayList<>(cache.values());
	}

}

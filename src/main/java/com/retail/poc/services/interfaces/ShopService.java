package com.retail.poc.services.interfaces;

import com.retail.poc.exceptions.ShopNotFoundException;
import com.retail.poc.model.Shop;
import com.retail.poc.model.ShopWithCoordinates;

public interface ShopService {
	ShopWithCoordinates saveOrUpdate(Shop shop);
	ShopWithCoordinates findClosestShopWithCoordinates(String customerLatitude, String customerLongitude) throws ShopNotFoundException;
}

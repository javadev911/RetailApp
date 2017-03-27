package com.retail.poc.controllers.interfaces;

import org.springframework.http.ResponseEntity;

import com.retail.poc.exceptions.ShopNotFoundException;
import com.retail.poc.model.Shop;

public interface ShopController {
	ResponseEntity<?> saveOrUpdateShop(Shop request);
	ResponseEntity<?> findNearestShop(String latitude, String longitude) throws ShopNotFoundException;
	ResponseEntity<?> getAllShops();
}

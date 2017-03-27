package com.retail.poc.controllers;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.retail.poc.controllers.interfaces.ShopController;
import com.retail.poc.exceptions.ShopNotFoundException;
import com.retail.poc.model.Shop;
import com.retail.poc.services.ShopServiceImpl;

@RestController
public class ShopControllerImpl implements ShopController {

	private final String RETAIL_CONTEXT = "/retail";

	@Autowired
	ShopServiceImpl retailService;

	@Override
	@PostMapping(RETAIL_CONTEXT + "/shops")
	public ResponseEntity<?> saveOrUpdateShop(@Valid @RequestBody Shop request) {
		return ResponseEntity.ok(retailService.saveOrUpdate(request));
	}

	@Override
	@GetMapping(RETAIL_CONTEXT + "/find-nearest-shop")
	public ResponseEntity<?> findNearestShop(@NotBlank String latitude,
			@NotBlank String longitude) throws ShopNotFoundException {

		try {
			return ResponseEntity.ok(retailService
					.findClosestShopWithCoordinates(latitude, longitude));
		} catch (ShopNotFoundException e) {
			throw e;
		}

	}
	
	@Override
	@GetMapping(RETAIL_CONTEXT + "/all-shops")
	public ResponseEntity<?> getAllShops(){
		return ResponseEntity.ok(retailService.getAllShops());
	}
}

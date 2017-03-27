package com.retail.poc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.retail.poc.AppConfig;
import com.retail.poc.exceptions.ShopNotFoundException;
import com.retail.poc.model.Coordinates;
import com.retail.poc.model.Shop;
import com.retail.poc.model.ShopAddress;
import com.retail.poc.model.ShopWithCoordinates;
import com.retail.poc.services.interfaces.GoogleService;
import com.retail.poc.services.interfaces.ShopService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })public class ShopServiceImplTest {

	private static final String SHOP_MARKS_AND_SPENCERS = "MARKS_AND_SPENCERS";
	private static final String SHOP_TESCO = "TESCO";
	private static final String CUSTOMER_LATITUDE = "51.5052433";
	private static final String CUSTOMER_LONGITUDE = "-0.0211143";

	@Mock
	private GoogleService googleService;
	
	@InjectMocks
	@Autowired
	private ShopService testClass;
	
	ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void test_saveOrUpdate() {
		
		//GIVEN
		Shop shop = new Shop();
		ShopAddress shopAddress = new ShopAddress();
		shop.setShopAddress(shopAddress);
		shop.setShopName("Canary Wharf Tesco Metro");
		shopAddress.setNumber("15");
		shopAddress.setPostCode("E14 4QT");
		
		final String address = generateAddressFromShop(shop);
		Coordinates coordinates = new Coordinates();
		coordinates.setLatitude(CUSTOMER_LATITUDE);
		coordinates.setLongitude(CUSTOMER_LONGITUDE);
		
		//WHEN
		when(googleService.getCoordinates(address)).thenReturn(coordinates);
		ShopWithCoordinates shopWithCoordinates = testClass.saveOrUpdate(shop);
		
		//THEN
		assertNotNull("shopWithCoordinates shouldn't be null!", shopWithCoordinates);
		assertEquals("Latitude didn't match", CUSTOMER_LATITUDE, shopWithCoordinates.getCoordinates().getLatitude());
		assertEquals("Longitude didn't match", CUSTOMER_LONGITUDE, shopWithCoordinates.getCoordinates().getLongitude());
		verify(googleService).getCoordinates(stringArgumentCaptor.capture());
		assertEquals("same address is not passed to googleService.getCoordinates method!", address, stringArgumentCaptor.getValue());
	}
	
	@Test
	public void test_findClosestShopWithCoordinates_throws_ShopNotFoundException() {
		try{
			testClass.findClosestShopWithCoordinates(CUSTOMER_LATITUDE, CUSTOMER_LONGITUDE);
		} catch(Exception e){
			assertTrue(e instanceof ShopNotFoundException);
			String expectedErrorMessage = "No shop found near your location for given coordinates. Latitude:"+CUSTOMER_LATITUDE+", Longitude:"+CUSTOMER_LONGITUDE;
			assertEquals("Error Message is not as expected. It should be: "+expectedErrorMessage, expectedErrorMessage, e.getMessage());
		}
				
	}
	
	@Test
	public void test_findClosestShopWithCoordinates_returns_closest_ShopWithCoordinates() {

		//GIVEN
		List<Shop> shops = getShops();
		prepareData(shops);
		
		try{
			//WHEN
			ShopWithCoordinates closestShopWithCoordinates = testClass.findClosestShopWithCoordinates(CUSTOMER_LATITUDE, CUSTOMER_LONGITUDE);
			
			//THEN
			assertNotNull("ClosestShopWithCoordinates cannot be null", closestShopWithCoordinates);
			assertEquals("Expected Closest Shop is MARKS_AND_SPENCERS", SHOP_MARKS_AND_SPENCERS, closestShopWithCoordinates.getShopName());
			
		} catch(ShopNotFoundException e){
			fail("This expection must not occur");
		}
				
	}

	private void prepareData(List<Shop> shops) {
		String address;
		Coordinates coordinates;
		for(Shop shop : shops){
			address = generateAddressFromShop(shop);
			coordinates = new Coordinates();
			if(SHOP_TESCO.equals(shop.getShopName())){
				setExpectationForShopTesco(coordinates);
			} else if(SHOP_MARKS_AND_SPENCERS.equals(shop.getShopName())){
				setExpectationForShopMarksAndSpencers(coordinates);
			}
			
			when(googleService.getCoordinates(address)).thenReturn(coordinates);
			
			testClass.saveOrUpdate(shop);
		}
	}

	private void setExpectationForShopMarksAndSpencers(Coordinates coordinates) {
		coordinates.setLatitude("51.5093499");
		coordinates.setLongitude("-0.0210632");
		when(googleService.distanceInMeters(
				Double.parseDouble(CUSTOMER_LATITUDE), 
				Double.parseDouble(CUSTOMER_LONGITUDE), 
				Double.parseDouble(coordinates.getLatitude()), 
				Double.parseDouble(coordinates.getLongitude()))).thenReturn(15.34256);
	}

	private void setExpectationForShopTesco(Coordinates coordinates) {
		coordinates.setLatitude("51.5124590");
		coordinates.setLongitude("-0.0234230");
		when(googleService.distanceInMeters(
				Double.parseDouble(CUSTOMER_LATITUDE), 
				Double.parseDouble(CUSTOMER_LONGITUDE), 
				Double.parseDouble(coordinates.getLatitude()), 
				Double.parseDouble(coordinates.getLongitude()))).thenReturn(19.98493);
	}
	
	private String generateAddressFromShop(Shop shop) {
		return new StringBuilder(shop.getShopName()).append("+")
				.append(shop.getShopAddress().getNumber()).append("+")
				.append(shop.getShopAddress().getPostCode()).toString();
	}
	
	private List<Shop> getShops(){
		
		List<Shop> shops = new ArrayList<>();
		
		//Shop1
		Shop shop1 = new Shop();
		ShopAddress shopAddress = new ShopAddress();
		shop1.setShopAddress(shopAddress);
		shops.add(shop1);
		
		shop1.setShopName(SHOP_TESCO);
		shopAddress.setNumber("15");
		shopAddress.setPostCode("E14 4QT");
		
		//Shop1
		Shop shop2 = new Shop();
		shopAddress = new ShopAddress();
		shop2.setShopAddress(shopAddress);
		shops.add(shop2);
		
		shop2.setShopName(SHOP_MARKS_AND_SPENCERS);
		shopAddress.setNumber("Cardinal Place Victoria Street");
		shopAddress.setPostCode("E14 4QQ");
		
		
		
		return shops;
		
	}


}

package com.retail.poc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.retail.poc.AppConfig;
import com.retail.poc.model.Coordinates;
import com.retail.poc.services.interfaces.GoogleService;
import com.retail.poc.util.DistanceCalculator;
import com.retail.poc.util.HttpConnectionUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
public class GoogleServiceTest {

	@Mock
	private HttpConnectionUtil httpConnectionUtil;
	@Mock
	private DistanceCalculator distanceCalculator;
	
	@InjectMocks
	@Autowired
	private GoogleService testClass;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test_getCoordinates_for_a_given_address() {

		// GIVEN
		String expectedAddress = "Canary Wharf Tesco Metro+15+E14 4QT";
		Coordinates expectedCoordinates = new Coordinates();
		expectedCoordinates.setLatitude("51.5052433");
		expectedCoordinates.setLongitude("-0.0211143");

		// WHEN
		when(httpConnectionUtil.getCoordinates(expectedAddress)).thenReturn(expectedCoordinates);
		Coordinates actualCoordinates = testClass.getCoordinates(expectedAddress);

		// THEN
		assertNotNull("Actual coordinates are null!", actualCoordinates);
		assertEquals("Given and Actual Latitudes are not equal", expectedCoordinates.getLatitude(), actualCoordinates.getLatitude());
		assertEquals("Given and Actual Longitudes are not equal", expectedCoordinates.getLatitude(), actualCoordinates.getLatitude());
		
		verify(httpConnectionUtil).getCoordinates(expectedAddress);
	}
	
	@Test
	public void test_distanceCalculator_returns_distance_between_two_lats_two_longs_in_meters(){
		
		final double EXPECTED_DISTANCE_IN_METERS = 12.4509456;
		final double DELTA = 0.0;
		
		// GIVEN
		double customerLatitude = 51.5083517;
		double customerLongitude = -0.2811731;
		double shopLatitude = 51.50835;
		double shopLongitude = 0.28117;
		
		//WHEN
		when(distanceCalculator.distanceInMeters(customerLatitude, customerLongitude, shopLatitude, shopLongitude)).thenReturn(EXPECTED_DISTANCE_IN_METERS);
		
		//THEN
		double actuakDistanceInMeters = testClass.distanceInMeters(customerLatitude, customerLongitude, shopLatitude, shopLongitude);
		assertEquals("", EXPECTED_DISTANCE_IN_METERS, actuakDistanceInMeters, DELTA);
		verify(distanceCalculator).distanceInMeters(customerLatitude, customerLongitude, shopLatitude, shopLongitude);
	}

}

package com.retail.poc.bdd.steps;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.poc.model.Shop;
import com.retail.poc.model.ShopAddress;
import com.retail.poc.model.ShopWithCoordinates;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:cucumber.xml"})
public class RetailAppSteps {
	
	private final String BASE_URL = "http://localhost:8080"; 
	private List<Shop> shops;
	private List<ShopAddress> addresses;
	private RestTemplate restTemplate;
	private String responseJSON;
	String customerLatitude;
	String customerLongitude;

	@Given("^shop names as$")
	public void shopDetailsAs(List<Shop> givenShops) throws Throwable {
		this.shops = givenShops; 
	}
	
	@And("^shop addresses as$")
	public void shopAddressesAs(List<ShopAddress> givenAddresses) throws Throwable {
		this.addresses = givenAddresses;
		updateShopsWithGivenAddresses(givenAddresses);
	}

	private void updateShopsWithGivenAddresses(List<ShopAddress> givenAddresses) {
		for(int index=0; index < givenAddresses.size(); index++){
			shops.get(index).setShopAddress(addresses.get(index));
		}
	}

	@Then("^saved or updated details should be returned in reponse$")
	public void savedOrUpdatedDetailsShouldBeReturnedInReponse()
			throws Throwable {
		ShopWithCoordinates shopWithCoordinates = getShopWithCoordinatesFromJSONString(responseJSON);
		assertEquals("Shop names must match", shops.get(0).getShopName(), shopWithCoordinates.getShopName());
	}

	@When("^I call shops API$")
	public void iCallShopsAPI() throws Throwable {
		ResponseEntity<String> response = saveOrUpdateShop(shops.get(0));
		responseJSON = response.getBody();
		
	}

	private ResponseEntity<String> saveOrUpdateShop(Shop shop)
			throws JsonProcessingException {
		String saveOrUpdateShopsAPI = new StringBuilder(BASE_URL).append("/retail/shops").toString();
		restTemplate = new RestTemplate();
		HttpHeaders headers = prepareHeader();
		
		String requestJson = getJSONString(shop);
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(saveOrUpdateShopsAPI, HttpMethod.POST, entity, String.class);
		return response;
	}


	@When("^I call findNearestShop API with my coordinates, latitude as \"(.*?)\", longitude as \"(.*?)\"$")
	public void iCallFindNearestShopAPIWithMyCoordinatesLatitudeAsLongitudeAs(
			String givenCustomerLatitude, String givenCustomerLongitude) throws Throwable {
		
		this.customerLatitude = givenCustomerLatitude;
		this.customerLongitude = givenCustomerLongitude;
		
		UriComponentsBuilder builder = getBuilder();
		HttpHeaders headers = prepareHeader();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		HttpEntity<String> response = findNearestShopAPIWithMyCoordinates(builder, entity);
		this.responseJSON = response.getBody();
	}

	private ResponseEntity<String> findNearestShopAPIWithMyCoordinates(
			UriComponentsBuilder builder, HttpEntity<?> entity) {
		return restTemplate.exchange(
		        builder.build().encode().toUri(), 
		        HttpMethod.GET, 
		        entity, 
		        String.class);
	}
	
	private UriComponentsBuilder getBuilder(){
		String findNearestShopAPIWithMyCoordinatesAPI = new StringBuilder(BASE_URL).append("/retail/find-nearest-shop").toString();
		restTemplate = new RestTemplate();
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(findNearestShopAPIWithMyCoordinatesAPI)
		        .queryParam("latitude", customerLatitude)
		        .queryParam("longitude", customerLongitude);
		return builder;
	}

	@And("^when these shop details are in application memory$")
	public void whenTheseShopDetailsAreInApplicationMemory() throws Throwable {
		for(Shop shop : shops){
			saveOrUpdateShop(shop);
		}
	}

	@Then("^nearest shop \"(.*?)\" should be returned$")
	public void nearestShopShouldBeReturned(String expectedStoreName) throws Throwable {
		ShopWithCoordinates shopWithCoordinates = getShopWithCoordinatesFromJSONString(responseJSON);
		assertEquals("expected Store name is: "+expectedStoreName, expectedStoreName, shopWithCoordinates.getShopName());
	}

	private ShopWithCoordinates getShopWithCoordinatesFromJSONString(
			String responseJSON) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(responseJSON, ShopWithCoordinates.class);
	}

	private HttpHeaders prepareHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

	private String getJSONString(Shop shop) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(shop);
	}

}

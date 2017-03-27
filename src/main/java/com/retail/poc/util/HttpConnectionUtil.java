package com.retail.poc.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.retail.poc.exceptions.GoogleAddressNotFoundException;
import com.retail.poc.model.Coordinates;

@Service
public class HttpConnectionUtil {
	
	private static final int STATUS_OK_CODE = 200;
	private static final String STATUS_OK = "OK";
	private static final String GEOCODE_RESPONSE_STATUS = "/GeocodeResponse/status";
	private static final String GOOGLE_ADDRESS_API = "http://maps.googleapis.com/maps/api/geocode/xml?address=";
	
	final static Logger LOGGER = Logger.getLogger(HttpConnectionUtil.class);
	
	public Coordinates getCoordinates(String address) {
		Coordinates coordinates = null;
		int responseCode = 0;
		URL url;
		try {
			url = new URL(prepareGoogleAddressURL(address));
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			httpConnection.connect();
			responseCode = httpConnection.getResponseCode();
			if (responseCode == STATUS_OK_CODE) {
				coordinates = getCoordinates(httpConnection);
			}
		} catch (IOException | XPathExpressionException
				| ParserConfigurationException | SAXException
				| GoogleAddressNotFoundException e) {
			LOGGER.error("Error Reported - Google Address API: " + e);
		}
		return coordinates;
	}
	
	private Coordinates getCoordinates(
			HttpURLConnection httpConnection)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, GoogleAddressNotFoundException {

		Coordinates coordinates = new Coordinates();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		Document document = builder.parse(httpConnection.getInputStream());
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(GEOCODE_RESPONSE_STATUS);
		String status = (String) expr.evaluate(document, XPathConstants.STRING);
		
		if (status.equals(STATUS_OK)) {
			coordinates.setLatitude(getLatitude(document, xpath));
			coordinates.setLongitude(getLongitude(document, xpath));
		} else {
			throw new GoogleAddressNotFoundException(
					"Error from Google Address API - response status: "
							+ status);
		}
		return coordinates;
	}

	private String getLatitude(Document document, XPath xpath)
			throws XPathExpressionException {
		return (String) xpath.compile("//geometry/location/lat").evaluate(
				document, XPathConstants.STRING);
	}

	private String getLongitude(Document document, XPath xpath)
			throws XPathExpressionException {
		return (String) xpath.compile("//geometry/location/lng").evaluate(
				document, XPathConstants.STRING);
	}

	private String prepareGoogleAddressURL(String address)
			throws UnsupportedEncodingException {
		return new StringBuilder(GOOGLE_ADDRESS_API)
				.append(URLEncoder.encode(address, "UTF-8"))
				.append("&sensor=true").toString();
	}
}

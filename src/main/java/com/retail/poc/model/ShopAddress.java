package com.retail.poc.model;

import org.hibernate.validator.constraints.NotBlank;

public class ShopAddress {
	
	@NotBlank(message = "number can't be empty!")
	private String number;
	@NotBlank(message = "postCode can't be empty!")
	private String postCode;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

}

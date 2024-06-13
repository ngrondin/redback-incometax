package io.redback.incometax.wms;

import io.firebus.data.DataMap;

public class Address {
	public String number;
	public String street;
	public String city;
	public String state;
	public String postcode;
	public String country;
	
	public Address(DataMap data) {
		if(data != null) {
			number = data.getString("number");
			street = data.getString("street");
			city = data.getString("city");
			state = data.getString("state");
			postcode = data.getString("postcode");
			country = data.getString("country");
		}
	}
	
	public String getLine1() {
		return ((number != null ? number : "") + " " + (street != null ? street : "")).trim();
	}
}

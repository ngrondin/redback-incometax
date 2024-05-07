package io.redback.incometax.wms;

public class Address {

	public String line1;
	public String city;
	public String state;
	public String postcode;
	
	public Address(String str) {
		if(str != null) {
			String[] parts = str.split(",");
			line1 = parts[0];
			String[] subparts = parts[1].split(" ");
			city = subparts[0];
			state = subparts[1];
			postcode = parts[2];
		}
	}
}

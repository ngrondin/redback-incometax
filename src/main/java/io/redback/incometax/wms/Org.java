package io.redback.incometax.wms;

import io.redback.client.RedbackObjectRemote;
import io.redback.exceptions.RedbackException;

public class Org {
	public String id;
	public String name;
	public Address address;
	public String taxId;
	public String businessId;
	public String contactName;
	public String contactEmail;
	public String contactPhone;
	
	public Org(RedbackObjectRemote taxorg, RedbackObjectRemote person) throws RedbackException {
		id = taxorg.getString("org");
		name = taxorg.getString("name");
		address = new Address(taxorg.getString("address"));
		taxId = taxorg.getString("taxid");
		businessId = taxorg.getString("businessid");
		if(person != null) {
			contactName = person.getString("fullname");
			contactEmail = person.getString("email");
			contactPhone = person.getString("phone");
		}
	}
	
}

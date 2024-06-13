package io.redback.incometax.wms;

import io.firebus.data.DataMap;
import io.redback.client.RedbackObjectRemote;
import io.redback.exceptions.RedbackException;

public class Org {
	public String id;
	public String name;
	public Address address;
	public String taxId;
	public String businessId;
	public String branchCode;
	public String contactName;
	public String contactEmail;
	public String contactPhone;
	
	public Org(RedbackObjectRemote taxorg, RedbackObjectRemote person) throws RedbackException {
		id = taxorg.getString("org");
		name = taxorg.getString("name");
		DataMap addressParts = (DataMap)taxorg.getObject("addressparts");
		address = new Address(addressParts);
		taxId = taxorg.getString("taxid");
		businessId = taxorg.getString("businessid");
		branchCode = taxorg.getString("branch");
		if(person != null) {
			contactName = person.getString("fullname");
			contactEmail = person.getString("email");
			contactPhone = person.getString("phone");
		}
	}
	
}

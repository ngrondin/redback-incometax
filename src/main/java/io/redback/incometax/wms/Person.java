package io.redback.incometax.wms;

import java.util.HashMap;
import java.util.Map;

import io.firebus.data.DataMap;
import io.redback.client.RedbackObjectRemote;
import io.redback.client.RedbackObjectRemoteList;
import io.redback.exceptions.RedbackException;

public class Person {
	public String id;
	public String firstname;
	public String lastname;
	public String phone;
	public String email;
	public String org;
	public String employeenum;
	public String taxid;
	public Address address;
	public double paybase;
	public Map<String, Object> attributes;
	
	public Person(RedbackObjectRemote p, RedbackObjectRemoteList a) throws RedbackException {
		id = p.getUid();
		firstname = p.getString("firstname");
		lastname = p.getString("lastname");
		phone = p.getString("phone");
		email = p.getString("email");
		org = p.getString("org");
		employeenum = p.getString("employeenum");
		taxid = p.getString("taxid");
		DataMap addressParts = (DataMap)p.getObject("homeaddressparts");
		address = new Address(addressParts);
		paybase = p.getNumber("paybase").doubleValue();
		attributes = new HashMap<String, Object>();
		if(a != null) {
			for(RedbackObjectRemote attrObj: a) {
				attributes.put(attrObj.getString("code"), attrObj.getObject("value"));
			}
			
		}
		
	}

}

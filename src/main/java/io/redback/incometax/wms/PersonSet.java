package io.redback.incometax.wms;

import java.util.ArrayList;
import java.util.List;

public class PersonSet extends ArrayList<Person> {
	
	private static final long serialVersionUID = 1L;

	public PersonSet() {
		super();
	}
	
	public List<String> getIds() {
		List<String> ret = new ArrayList<String>();
		for(Person person: this) ret.add(person.id);
		return ret;
	}
		
	public List<String> uniqueOrgIds() {
		List<String> ret = new ArrayList<String>();
		for(Person person: this) {
			String id = person.org;
			if(!ret.contains(id)) ret.add(id);
		}
		return ret;
	}
	
	public PersonSet forOrg(String org) {
		PersonSet ret = new PersonSet();
		for(Person person: this) if(person.org.equals(org)) ret.add(person);
		return ret;
	}
}

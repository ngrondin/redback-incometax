package io.redback.incometax.wms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.redback.client.RedbackObjectRemote;
import io.redback.client.RedbackObjectRemoteList;
import io.redback.exceptions.RedbackException;

public class PayLineSet extends ArrayList<PayLine> {
	
	private static final long serialVersionUID = 1L;

	public PayLineSet() {
		super();
	}
	
	public PayLineSet(RedbackObjectRemoteList list, Map<String, PayItem> itemMap) throws RedbackException {
		for(RedbackObjectRemote ror: list) {
			this.add(new PayLine(ror, itemMap));
		}
	}
	
	public PayLineSet forPerson(String person) {
		PayLineSet ret = new PayLineSet();
		for(PayLine line: this) if(line.person.equals(person)) ret.add(line);
		return ret;
	}
	
	public PayLineSet forPeople(PersonSet persons) {
		List<String> personIds = persons.getIds();
		PayLineSet ret = new PayLineSet();
		for(PayLine line: this) if(personIds.contains(line.person)) ret.add(line);
		return ret;
	}

	public PayLineSet forPayrun(String payrun) {
		PayLineSet ret = new PayLineSet();
		for(PayLine line: this) if(line.payrun.equals(payrun)) ret.add(line);
		return ret;
	}
	
	public PayLineSet forCategory(String category) {
		PayLineSet ret = new PayLineSet();
		for(PayLine line: this) if(line.category.equals(category)) ret.add(line);
		return ret;
	}

	public PayLineSet forCode(String code) {
		PayLineSet ret = new PayLineSet();
		for(PayLine line: this) if(line.hasCode(code)) ret.add(line);
		return ret;
	}
	
	public PayLineSet forTaxable(boolean t) {
		PayLineSet ret = new PayLineSet();
		for(PayLine line: this) if(line.item.taxExempt == !t) ret.add(line);
		return ret;	
	}
	
	public double sum() {
		double val = 0;
		for(PayLine line: this) val += line.value;
		return val;
	}
	
	public List<String> uniqueEndCodes() {
		List<String> ret = new ArrayList<String>();
		for(PayLine line: this) {
			String code = line.endCode();
			if(!ret.contains(code)) ret.add(code);
		}
		return ret;
	}
	
	public List<String> uniquePersonIds() {
		List<String> ret = new ArrayList<String>();
		for(PayLine line: this) {
			String personId = line.person;
			if(!ret.contains(personId)) ret.add(personId);
		}
		return ret;
	}
}

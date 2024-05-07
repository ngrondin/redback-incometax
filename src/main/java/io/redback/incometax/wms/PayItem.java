package io.redback.incometax.wms;

import java.util.List;

import io.redback.client.RedbackObjectRemote;
import io.redback.exceptions.RedbackException;

public class PayItem {
	public String id;
	public String name;
	public String category;
	public boolean taxExempt;
	public List<String> codes;
	
	public PayItem(RedbackObjectRemote o, List<String> c) throws RedbackException {
		id = o.getUid();
		name = o.getString("name");
		category = o.getString("category");
		taxExempt = o.getBool("taxexempt");
		codes = c;
	}
	
	public boolean hasCode(String c) {
		return codes != null ? codes.contains(c) : false;
	}
	
	public String endCode() {
		return codes != null && codes.size() > 0 ? codes.get(0) : null;
	}
}

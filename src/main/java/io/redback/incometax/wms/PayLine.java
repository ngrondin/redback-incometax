package io.redback.incometax.wms;

import java.util.Map;

import io.redback.client.RedbackObjectRemote;
import io.redback.exceptions.RedbackException;

public class PayLine {
	public double quantity;
	public double value;
	public String person;
	public String payrun;
	public String category;
	public PayItem item;
	
	public PayLine(RedbackObjectRemote o, Map<String, PayItem> itemMap) throws RedbackException {
		String itemId = o.getString("item");
		quantity = o.getNumber("quantity").doubleValue();
		value = o.getNumber("value").doubleValue();
		person = o.getString("person");
		payrun = o.getString("payrun");
		category = o.getString("category");
		item = itemMap.get(itemId);
	}
	
	public boolean hasCode(String code) {
		return item != null ? item.hasCode(code) : false;
	}
	
	public String endCode() {
		return item != null ? item.endCode() : null;
	}

}

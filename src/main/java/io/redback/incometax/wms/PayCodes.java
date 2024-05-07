package io.redback.incometax.wms;

import java.util.List;

public class PayCodes {
	public List<String> codes;
	
	public PayCodes(List<String> c) {
		codes = c;
	}
	
	public boolean contains(String c) {
		return codes != null ? codes.contains(c) : false;
	}
	
	public String endCode() {
		return codes != null && codes.size() > 0 ? codes.get(0) : null;
	}
	
}

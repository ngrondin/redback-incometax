package io.redback.incometax.wms;

import java.util.ArrayList;

public class PayrunIntegrationSet extends ArrayList<PayrunIntegration> {
	
	private static final long serialVersionUID = 1L;

	public PayrunIntegrationSet() {
		super();
	}
	
	public boolean hasSuccessfulIntegration() {
		boolean hasSuccess = false;
		for(PayrunIntegration integ: this) {
			if(integ.success) hasSuccess = true;
		}
		return hasSuccess;
	}
	
}

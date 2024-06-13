package io.redback.incometax.wms;

import io.redback.client.RedbackObjectRemote;
import io.redback.exceptions.RedbackException;

public class PayrunIntegration {
	public String payrun;
	public boolean success;
	public String id;
	
	public PayrunIntegration(RedbackObjectRemote ir) throws RedbackException {
		id = ir.getString("id");
		success = ir.getString("status").equals("success");
		payrun = ir.getString("objectuid");
	}
}

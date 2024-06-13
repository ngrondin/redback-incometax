package io.redback.incometax.wms;

import java.util.Date;

import io.redback.client.RedbackObjectRemote;
import io.redback.exceptions.RedbackException;

public class Payrun {
	public String id;
	public String code;
	public Date start;
	public Date end;
	public String timezone;
	public String overrides;
	
	public Payrun(RedbackObjectRemote pr) throws RedbackException {
		id = pr.getUid();
		code = pr.getString("code");
		start = pr.getDate("startdate");
		end = pr.getDate("enddate");
		timezone = pr.getString("timezone");
		overrides = pr.getString("overrides");
	}
}

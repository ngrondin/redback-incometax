package io.redback.incometax.wms;
import io.firebus.Firebus;
import io.firebus.Payload;
import io.firebus.data.DataMap;
import io.firebus.exceptions.FunctionErrorException;
import io.firebus.information.ServiceInformation;
import io.redback.exceptions.RedbackException;
import io.redback.exceptions.RedbackUnauthorisedException;
import io.redback.security.Session;
import io.redback.services.common.AuthenticatedServiceProvider;

public abstract class IncomeTaxService extends AuthenticatedServiceProvider {

	public IncomeTaxService(String n, DataMap c, Firebus f) throws RedbackException {
		super(n, c, f);
	}
	
	public ServiceInformation getServiceInformation() {
		return null;
	}

	public Payload redbackAuthenticatedService(Session session, Payload payload) throws RedbackException {
		Payload response = null;
		try
		{
			DataMap request = new DataMap(payload.getString());
			String action = request.getString("action");
			String payrunid = request.getString("payrun");
			if(action != null)
			{
				if(action.equals("sendpayrun")) 
				{
					String receiptId = sendPayrun(session, payrunid);					
					response = new Payload(new DataMap("id", receiptId));
				}
				else if(action.equals("addtax")) 
				{
					String personid = request.getString("person");
					addTaxLines(session, payrunid, personid);					
					response = new Payload(new DataMap("result", "ok"));
				}
				else
				{
					throw new FunctionErrorException("The 'action' provided is not recognized");
				}
			}
			else
			{
				throw new FunctionErrorException("The Income Tax service requires an 'action' value");
				
			}
		}
		catch(Exception e)
		{
			throw new RedbackException("Error calling Income Taxs service", e);
		}
		return response;	
	}

	public Payload redbackUnauthenticatedService(Session session, Payload payload) throws RedbackException {
		throw new RedbackUnauthorisedException("All ATO services need to be authenticated");
	}
	
	protected abstract String sendPayrun(Session session, String payrunUid) throws RedbackException;
	
	protected abstract void addTaxLines(Session session, String payrunUid, String personUid) throws RedbackException;
	
}

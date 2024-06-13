package io.redback.incometax.wms;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.firebus.Firebus;
import io.firebus.data.DataList;
import io.firebus.data.DataMap;
import io.redback.client.DataClient;
import io.redback.client.FileClient;
import io.redback.client.IntegrationClient;
import io.redback.client.ObjectClient;
import io.redback.client.RedbackObjectRemote;
import io.redback.client.RedbackObjectRemoteList;
import io.redback.exceptions.RedbackException;
import io.redback.security.Session;
import io.redback.utils.Convert;

public abstract class IncomeTaxManager {
	protected String name;
	protected Firebus firebus;
	protected String dataServiceName;
	protected String objectServiceName;
	protected String fileServiceName;
	protected String integrationServiceName;
	protected DataClient dataClient;
	protected ObjectClient objectClient;
	protected FileClient fileClient;
	protected IntegrationClient integrationClient;
	protected String incomeTaxId;

	public IncomeTaxManager(String n, DataMap config, Firebus fb) throws RedbackException
	{
		try {
			name = n;
			firebus = fb;
			dataServiceName = config.getString("dataservice");
			objectServiceName = config.getString("objectservice");
			fileServiceName = config.getString("fileservice");
			integrationServiceName = config.getString("integrationservice");

			dataClient = new DataClient(firebus, dataServiceName);
			objectClient = new ObjectClient(firebus, objectServiceName);
			fileClient = new FileClient(firebus, fileServiceName);
			integrationClient = new IntegrationClient(firebus, integrationServiceName);
			
			incomeTaxId = config.getString("incometax");
			
		} catch(Exception e) {
			throw new RedbackException("Error initialising Income Tax Manager", e);
		}	
	}
	
	protected Map<String, PayItem> getPayItems(Session session) throws RedbackException {
		Map<String, PayItem> payItemMap = new HashMap<String, PayItem>();
		RedbackObjectRemoteList items = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "payitem", new DataMap(), null, false));
		RedbackObjectRemoteList mapItems = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "payitemmap", new DataMap("incometax", incomeTaxId), null, false));
		RedbackObjectRemoteList codes = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "incometaxcode", new DataMap("incometax", incomeTaxId), null, false));
		for(RedbackObjectRemote item: items) {
			String itemId = item.getUid();
			List<String> codeHierarchy = null;
			RedbackObjectRemote mapItem = mapItems.find("payitem", itemId);
			if(mapItem != null) {
				String codeId = mapItem.getString("incometaxcode");
				RedbackObjectRemote codeObj = codes.find("uid", codeId);
				if(codeObj != null) {
					codeHierarchy = codes.getHierarchyOf(codeObj, "code", "parent", "uid");					
					payItemMap.put(itemId, new PayItem(item, codeHierarchy));
				}
			}
		}
		return payItemMap;
	}
	
	protected PayItem getTaxPayItem(Session session) throws RedbackException {
		RedbackObjectRemoteList items = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "payitem", new DataMap("category", "tax", "incometax", incomeTaxId), null, false));
		if(items.size() > 0) {
			PayItem payItem = new PayItem(items.get(0), null);
			return payItem;
		}
		return null;
	}
		
	protected Payrun getPayrun(Session session, String payrunId) throws RedbackException {
		RedbackObjectRemote ror = objectClient.getObject(session, "payrun", payrunId);
		Payrun payrun = new Payrun(ror);
		return payrun;
	}
	
	protected PayrunIntegrationSet getPayrunIntegrations(Session session, String payrunId) throws RedbackException {
		RedbackObjectRemoteList intResultList = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "intresult", new DataMap("name", "ATO", "object", "payrun", "objectuid", payrunId), null, false));
		PayrunIntegrationSet ret = new PayrunIntegrationSet();
		for(RedbackObjectRemote payrunIntegration: intResultList) {
			ret.add(new PayrunIntegration(payrunIntegration));
		}
		return ret;
	}
	
	protected PersonSet getPersons(Session session, DataList personIds) throws RedbackException {
		RedbackObjectRemoteList persons = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "person", new DataMap("uid", new DataMap("$in", personIds)), null, false));
		RedbackObjectRemoteList personAttributes = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "persontaxattribute", new DataMap("person", new DataMap("$in", personIds)), null, false));
		PersonSet ret = new PersonSet();
		for(RedbackObjectRemote person: persons) {
			String personId = person.getUid();
			RedbackObjectRemoteList thisPersonAttributes = personAttributes.filter("person", personId);
			ret.add(new Person(person, thisPersonAttributes));
		}
		return ret;		
	}
	
	protected PersonSet getPersons(Session session, String payrunId) throws RedbackException {
		RedbackObjectRemoteList payPersons = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "payrunperson", new DataMap("payrun", payrunId), null, false));
		DataList personIds = Convert.listToDataList(payPersons.uniqueListOfAttribute("person"));
		return getPersons(session, personIds);	
	}
	
	protected OrgSet getOrgsForPeople(Session session, PersonSet people) throws RedbackException {
		DataList orgIds = Convert.listToDataList(people.uniqueOrgIds());
		RedbackObjectRemoteList orgTaxIdentities = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "orgtaxidentity", new DataMap("org", new DataMap("$in", orgIds)), null, false));
		DataList personIds = Convert.listToDataList(orgTaxIdentities.uniqueListOfAttribute("contact"));
		RedbackObjectRemoteList persons = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "person", new DataMap("uid", new DataMap("$in", personIds)), null, false));
		OrgSet ret = new OrgSet();
		for(RedbackObjectRemote orgTaxIdentity: orgTaxIdentities) {
			String personId = orgTaxIdentity.getString("contact");
			RedbackObjectRemote person = persons.find("uid", personId);			
			ret.add(new Org(orgTaxIdentity, person));
		}
		return ret;
	}
	
	protected PayLineSet getPayLines(Session session, String payrunId) throws RedbackException {
		RedbackObjectRemoteList paylines = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "payrunline", new DataMap("payrun", payrunId), null, false));
		PayLineSet payLineSet = new PayLineSet(paylines, getPayItems(session));
		return payLineSet;
	}
	
	protected PayLineSet getAllPaylinesForPeopleSince(Session session, PersonSet people, Date since) throws RedbackException {
		RedbackObjectRemoteList payruns = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "payrun", new DataMap("enddate", new DataMap("$gt", since)), null, false));
		DataList payrunIds = Convert.listToDataList(payruns.uniqueListOfAttribute("uid"));
		DataList personIds = Convert.listToDataList(people.getIds());
		DataMap filter = new DataMap("person", new DataMap("$in", personIds), "payrun", new DataMap("$in", payrunIds));
		RedbackObjectRemoteList paylines = new RedbackObjectRemoteList(objectClient.listAllObjects(session, "payrunline", filter, null, false));
		PayLineSet payLineSet = new PayLineSet(paylines, getPayItems(session));
		return payLineSet;
	}
	
	protected void addPayLine(Session session, Payrun payrun, Person person, PayItem item, double quantity, double value) throws RedbackException {
		DataMap filter = new DataMap();
		filter.put("payrun", payrun.id);
		filter.put("item", item.id);
		filter.put("category", item.category);
		filter.put("person", person.id);
		List<RedbackObjectRemote> list = objectClient.listObjects(session, "payrunline", filter);
		if(list.size() > 1) 
			for(int i = 1; i < list.size(); i++) 
				objectClient.deleteObject(session, "payrunline", list.get(i).getUid());
		if(list.size() > 0) {
			DataMap data = new DataMap();
			data.put("quantity", quantity);
			data.put("value", value);
			objectClient.updateObject(session, "payrunline", list.get(0).getUid(), data, false);
		} else {
			DataMap data = new DataMap();
			data.put("payrun", payrun.id);
			data.put("item", item.id);
			data.put("category", item.category);
			data.put("person", person.id);
			data.put("quantity", quantity);
			data.put("value", value);
			objectClient.createObject(session, "payrunline", data, false);			
		}
	}
	
	protected void prepareIntegrationResult(Session session, Payrun payrun, String msgUid) throws RedbackException {
		DataMap filter = new DataMap("object", "payrun", "objectuid", payrun.id, "id", msgUid, "name", "ATO");
		DataMap data = new DataMap("status", "waiting");
		List<RedbackObjectRemote> list = objectClient.listObjects(session, "intresult", filter);
		if(list.size() > 0) {
			RedbackObjectRemote result = list.get(0);
			result.set(data);
		} else {
			DataMap fullData = filter.merge(data);
			objectClient.createObject(session, "intresult", fullData, false);
		}
	}
	
	protected void updateIntegrationResult(Session session, Payrun payrun, String msgUid, boolean success, String details) throws RedbackException {
		String status = success ? "success" : "error";
		DataMap filter = new DataMap("object", "payrun", "objectuid", payrun.id, "id", msgUid, "name", "ATO");
		DataMap data = new DataMap("status", status, "completed", new Date(), "details", details);
		List<RedbackObjectRemote> list = objectClient.listObjects(session, "intresult", filter);
		if(list.size() > 0) {
			RedbackObjectRemote result = list.get(0);
			result.set(data);
		} else {
			DataMap fullData = filter.merge(data);
			objectClient.createObject(session, "intresult", fullData, false);
		}
	}
}

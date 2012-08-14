package org.vamdc.tapservice;


import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.tapservice.api.TAPInterface;
import org.vamdc.tapservice.util.DBPlugTalker;




@Path("/TAP")
public class TAPImpl implements TAPInterface {
	//So, here we process the request :)
	@Override
	public Response getSyncResponse(String requestType, String version,
			String queryLang, String query, String outputFormat,
			Integer recordsLimit, String RequestID) {
		
		
		RequestProcess myrequest = new RequestProcess(query,DBPlugTalker.getRestrictables());
		
		/*requestType,version,queryLang,outputFormat,recordsLimit,RequestID are ignored for now*/
		if (myrequest.Valid){
			DBPlugTalker.buildXSAMS(myrequest);
		};
		
		//Return document
		myrequest.finishRequest();
		return myrequest.getResponse().build();
	}

	@Override
	public Response postSyncQuery(String requestType, String version,
			String queryLang, String query, String outputFormat,
			Integer recordsLimit, String RequestID) {
		return this.getSyncResponse(requestType, version, queryLang, query, outputFormat, recordsLimit, RequestID);
		
	}

	//TODO: verify HEAD requests processing 
	@Override
	public Response getSyncHead(String requestType, String version,
			String queryLang, String query, String outputFormat,
			Integer recordsLimit, String RequestID) {
		
		//Parse query, init request
		RequestProcess myrequest = new RequestProcess(query,DBPlugTalker.getRestrictables());
		
		Map<HeaderMetrics,Integer> metrics = null;
		//If request is valid, estimate sizes
		if (myrequest.isValid()){
			metrics=DBPlugTalker.getMetrics(myrequest);
		};
		
		//Return document
		myrequest.finishRequest();
		return myrequest.getHeadResponse(metrics).build();
	}
}

package org.vamdc.tapservice;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.tapservice.api.TAPInterface;
import org.vamdc.tapservice.util.DBPlugTalker;


@Path("/TAP")
public class TAPImpl implements TAPInterface {
	@Override
	public Response getSyncResponse(HttpServletRequest requestHttp ,String requestType, String version,
			String queryLang, String query, String outputFormat,
			Integer recordsLimit, String RequestID) {
		
		
		RequestProcess myrequest = new RequestProcess(query,DBPlugTalker.getRestrictables());
		
		verifyParameters(queryLang, outputFormat, myrequest);
		
		if (myrequest.isValid()){
			DBPlugTalker.buildXSAMS(myrequest);
		};
		
		//Return the document
		myrequest.finishRequest();
		Response res = myrequest.getResponse();
		new QueryStoreNotification(requestHttp,res, myrequest);
		return res;
	}

	@Override
	public Response postSyncQuery(HttpServletRequest requestHttp, String requestType, String version,
			String queryLang, String query, String outputFormat,
			Integer recordsLimit, String RequestID) {
		return this.getSyncResponse(requestHttp,requestType, version, queryLang, query, outputFormat, recordsLimit, RequestID);
	}

	@Override
	public Response getSyncHead(HttpServletRequest requestHttp, String requestType, String version,
			String queryLang, String query, String outputFormat,
			Integer recordsLimit, String RequestID) {
		
		//Parse query, init request
		RequestProcess myrequest = new RequestProcess(query,DBPlugTalker.getRestrictables());
		
		verifyParameters(queryLang, outputFormat, myrequest);
		
		Map<HeaderMetrics,Object> metrics = null;
		//If request is valid, estimate sizes
		if (myrequest.isValid()){
			metrics=DBPlugTalker.getMetrics(myrequest);
		};
		
		//Return the document headers
		myrequest.finishRequest();
		Response res = myrequest.getHeadResponse(metrics).build();
		new QueryStoreNotification(requestHttp,res, myrequest);
		return res;
	}
	
	private void verifyParameters(String queryLang, String outputFormat,
			RequestProcess myrequest) {
		if (!"XSAMS".equalsIgnoreCase(outputFormat))
			myrequest.addError("Only XSAMS output format (FORMAT parameter) is supported");
		if (queryLang==null || !queryLang.startsWith("VSS"))
			myrequest.addError("Query language (LANG parameter) should be VSS2 or VSS1");
	}
}

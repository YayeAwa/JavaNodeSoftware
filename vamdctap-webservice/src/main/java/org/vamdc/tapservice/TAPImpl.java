package org.vamdc.tapservice;


import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.tapservice.api.TAPInterface;
import org.vamdc.tapservice.util.DBPlugTalker;


@Path("/TAP")
public class TAPImpl implements TAPInterface {
	@Override
	@Path("/sync")
	@GET
	@Produces({"application/x-xsams+xml","application/x-votable+xml","text/xml"})
	public Response getSyncResponse(String requestType, String version,
			String queryLang, String query, String outputFormat,
			Integer recordsLimit, String RequestID) {
		
		
		RequestProcess myrequest = new RequestProcess(query,DBPlugTalker.getRestrictables());
		
		verifyParameters(queryLang, outputFormat, myrequest);
		
		if (myrequest.isValid()){
			DBPlugTalker.buildXSAMS(myrequest);
		};
		
		//Return the document
		myrequest.finishRequest();
		return myrequest.getResponse();
	}

	@Override
	@POST
	@Path("/sync")
	@Produces({"application/x-xsams+xml","application/x-votable+xml","text/xml"})
	public Response postSyncQuery(String requestType, String version,
			String queryLang, String query, String outputFormat,
			Integer recordsLimit, String RequestID) {
		return this.getSyncResponse(requestType, version, queryLang, query, outputFormat, recordsLimit, RequestID);
		
	}

	@Override
	@Path("/sync")
	@HEAD
	@Produces({"application/x-xsams+xml","application/x-votable+xml","text/xml"})
	public Response getSyncHead(String requestType, String version,
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
		return myrequest.getHeadResponse(metrics).build();
	}
	
	private void verifyParameters(String queryLang, String outputFormat,
			RequestProcess myrequest) {
		if (!"XSAMS".equalsIgnoreCase(outputFormat))
			myrequest.addError("Only XSAMS output format (FORMAT parameter) is supported");
		if (queryLang==null || !queryLang.startsWith("VSS"))
			myrequest.addError("Query language (LANG parameter) should be VSS2 or VSS1");
	}
}

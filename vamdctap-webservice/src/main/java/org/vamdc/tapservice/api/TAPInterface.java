package org.vamdc.tapservice.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;



/**
 * sync VAMDC-TAP, able to return XSAMS or votable 
 * See http://vamdc.org/documents/standards/ for VAMDC-TAP description
 */
public interface TAPInterface {
	@Path("/sync")
	@GET
	@Produces({"application/x-xsams+xml","application/x-votable+xml","text/xml"})
	public abstract Object getSyncResponse(
			@QueryParam("REQUEST") String requestType,
			@QueryParam("VERSION") String version,
			@QueryParam("LANG") String queryLang,
			@QueryParam("QUERY") String query,
			@QueryParam("FORMAT") String outputFormat,
			@QueryParam("MAXREC") Integer recordsLimit,
			@QueryParam("RUNID") String RequestID			
			);
	
	@Path("/sync")
	@POST
	@Produces({"application/x-xsams+xml","application/x-votable+xml","text/xml"})
	public abstract Object postSyncQuery(
			@FormParam("REQUEST") String requestType,
			@FormParam("VERSION") String version,
			@FormParam("LANG") String queryLang,
			@FormParam("QUERY") String query,
			@FormParam("FORMAT") String outputFormat,
			@FormParam("MAXREC") Integer recordsLimit,
			@FormParam("RUNID") String RequestID			
			);
	
	@Path("/sync")
	@HEAD
	@Produces({"application/x-xsams+xml","application/x-votable+xml","text/xml"})
	public abstract Object getSyncHead(
			@QueryParam("REQUEST") String requestType,
			@QueryParam("VERSION") String version,
			@QueryParam("LANG") String queryLang,
			@QueryParam("QUERY") String query,
			@QueryParam("FORMAT") String outputFormat,
			@QueryParam("MAXREC") Integer recordsLimit,
			@QueryParam("RUNID") String RequestID			
			);
}
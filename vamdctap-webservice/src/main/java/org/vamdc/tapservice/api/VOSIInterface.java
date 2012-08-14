package org.vamdc.tapservice.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.ivoa.xml.vosiavailability.v1.Availability;
import net.ivoa.xml.vosicapabilities.v1.Capabilities;

public interface VOSIInterface {
	
	//availability
	@Path("/availability")
	@Produces("text/xml")
	@GET
	public abstract Availability getAvailaibility();
	
	//capabilities
	@Path("/capabilities")
	@Produces("text/xml")
	@GET
	public abstract Capabilities getCapabilities();
	

}

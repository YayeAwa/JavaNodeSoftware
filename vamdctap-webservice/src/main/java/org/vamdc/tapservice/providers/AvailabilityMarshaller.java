package org.vamdc.tapservice.providers;

import java.io.IOException;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import net.ivoa.xml.vosiavailability.v1.Availability;

@Provider
@Produces("text/xml")
public class AvailabilityMarshaller extends JAXBMarshaller<Availability>{

	public AvailabilityMarshaller() throws IOException {
		super(Availability.class);
		this.XSLTHref="../static/availability.xsl";
	}
	

}

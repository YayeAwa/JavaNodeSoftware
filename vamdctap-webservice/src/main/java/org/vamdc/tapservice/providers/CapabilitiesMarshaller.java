package org.vamdc.tapservice.providers;

import java.io.IOException;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import net.ivoa.xml.vosicapabilities.v1.Capabilities;

@Provider
@Produces("text/xml")
public class CapabilitiesMarshaller extends JAXBMarshaller<Capabilities> {

	public CapabilitiesMarshaller() throws IOException {
		super(Capabilities.class);
		this.XSLTHref="../static/capabilities.xsl";
	}

}

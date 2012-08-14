package org.vamdc.tapservice.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.ivoa.xml.vosiavailability.v1.Availability;
import net.ivoa.xml.vosicapabilities.v1.Capabilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vamdc.tapservice.util.Settings;

@Provider
@Produces("text/xml")
public class IVOAMarshaller implements MessageBodyWriter {
	//TODO: implement generics here?
	JAXBContext storedContext;
	Marshaller capabilitiesMarshaller;
	Marshaller availabilityMarshaller;
	private Logger logger;
	
	public IVOAMarshaller() throws IOException{
		logger = LoggerFactory.getLogger("org.vamdc.tapservice");
		logger.debug("Init IVOA marshaller");
		try {

			storedContext=JAXBContext.newInstance(IVOAResolver.supportedCases);
			capabilitiesMarshaller=storedContext.createMarshaller();
			capabilitiesMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Settings.isTrue(Settings.SETTING_OUTPUT_PRETTYPRINT));
			capabilitiesMarshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml-stylesheet type='text/xsl' href='../static/capabilities.xsl' ?>");
			availabilityMarshaller=storedContext.createMarshaller();
			availabilityMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Settings.isTrue(Settings.SETTING_OUTPUT_PRETTYPRINT));
			availabilityMarshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml-stylesheet type='text/xsl' href='../static/availability.xsl' ?>");
			
		}catch (JAXBException e) {
			throw new IOException(e.getMessage());
		}
		
	}
	
	@Override
	public long getSize(Object arg0, Class arg1, Type arg2, Annotation[] arg3,
			MediaType arg4) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class arg0, Type xmltype, Annotation[] arg2,
			MediaType arg3) {
		logger.debug("Check if we are writeable with "+xmltype.toString());
		return xmltype.equals(Availability.class) || xmltype.equals(Capabilities.class);
	}

	@Override
	public void writeTo(Object obj, Class arg1, Type xmltype, Annotation[] arg3,
			MediaType arg4, MultivaluedMap arg5, OutputStream ostream)
			throws IOException, WebApplicationException {
		try {
			if (storedContext != null ){
					if (xmltype.equals(Availability.class))
						availabilityMarshaller.marshal(obj, ostream);
					else if (xmltype.equals(Capabilities.class))
						capabilitiesMarshaller.marshal(obj, ostream);
			}
		} catch (JAXBException e) {
			throw new IOException(e.getMessage());
		}
		
	}

}

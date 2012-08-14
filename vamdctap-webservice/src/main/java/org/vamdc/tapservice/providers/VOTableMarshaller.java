package org.vamdc.tapservice.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vamdc.tapservice.util.Settings;


/*
 * Here we marshall VOTable document
 * */

@Provider
@Produces("application/x-votable+xml")
public class VOTableMarshaller implements MessageBodyWriter {

	private JAXBContext storedContext;
	private Marshaller storedMarshaller;
	private Logger logger;
	//TODO: revise everything
	public VOTableMarshaller() throws IOException{
		logger = LoggerFactory.getLogger("org.vamdc.tapservice");
		logger.debug("Init VOTable marshaller");
		try {
			storedContext=JAXBContext.newInstance(net.ivoa.xml.votable.v1.VOTABLE.class);
			storedMarshaller=storedContext.createMarshaller();
			storedMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Settings.isTrue(Settings.SETTING_OUTPUT_PRETTYPRINT));
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
	public boolean isWriteable(Class arg0, Type arg1, Annotation[] arg2,
			MediaType arg3) {
		logger.debug("Check if we are writeable with "+arg1.toString());
		return net.ivoa.xml.votable.v1.VOTABLE.class.equals(arg1) || org.vamdc.tapservice.VOTableError.class.equals(arg1);
	}

	@Override
	public void writeTo(Object arg0, Class arg1, Type arg2, Annotation[] arg3,
			MediaType arg4, MultivaluedMap arg5, OutputStream arg6)
			throws IOException, WebApplicationException {
		try {
			if (storedContext != null && storedMarshaller!=null){
					storedMarshaller.marshal(arg0, arg6);
					logger.debug("Marshalled document");
			}
		} catch (JAXBException e) {
			throw new IOException(e.getMessage());
		}
		
		
	}


}

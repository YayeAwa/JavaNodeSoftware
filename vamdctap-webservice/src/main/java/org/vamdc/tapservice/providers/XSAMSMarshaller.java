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
import javax.xml.bind.JAXBException;

import org.vamdc.tapservice.util.Setting;
import org.vamdc.xsams.io.IOSettings;
import org.vamdc.xsams.io.JAXBContextFactory;
import org.vamdc.xsams.schema.XSAMSData;


/**
 * Here we marshall XSAMSRoot
 */
@Provider
@Produces("application/x-xsams+xml")
public class XSAMSMarshaller implements MessageBodyWriter<XSAMSData> {

	public XSAMSMarshaller() throws IOException{
		JAXBContextFactory.getContext();
		IOSettings.prettyprint.setIntValue(0);
		if (Setting.xsams_prettyprint.isEnabled())
			IOSettings.prettyprint.setIntValue(1);

	}

	@Override
	public long getSize(XSAMSData arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
			MediaType arg4) {
		return -1;
	}


	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] arg2,
			MediaType arg3) {
		return XSAMSData.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(XSAMSData arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
			MediaType arg4, MultivaluedMap<String, Object> arg5, OutputStream arg6)
					throws IOException, WebApplicationException {
		try {
			JAXBContextFactory.getMarshaller().marshal(arg0, arg6);
		} catch (JAXBException e) {
			throw new IOException(e.getMessage());
		}
	}

}

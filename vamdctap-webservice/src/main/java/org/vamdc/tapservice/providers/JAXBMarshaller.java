package org.vamdc.tapservice.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract JAXB marshaller implementation, to be extended to provide schema-specific marshallers
 *
 * @param <T> root mapping class of the xml schema
 */
public abstract class JAXBMarshaller<T> implements MessageBodyWriter<T> {

	private Logger logger;
	
	private JAXBContext storedContext;
	
	private Class<T> myType;
	
	/**
	 * To be defined if XSLT transformation stylesheet needs to be attached
	 */
	protected String XSLTHref=null;
	
	public JAXBMarshaller(Class<T> type) throws IOException{
		logger = LoggerFactory.getLogger("org.vamdc.tap");
		logger.debug("Init marshaller "+this.getClass());
		
		myType=type;
		
		try {
			storedContext=JAXBContext.newInstance(type);
		}catch (JAXBException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		logger.debug("Check if we are writeable with "+type.toString());
		return myType.isAssignableFrom(type);
	}
	
	@Override
	public long getSize(T t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}
	
	@Override
	public void writeTo(T t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		try {
			if (storedContext != null){
					getMarshaller().marshal(t, entityStream);
					logger.debug("Marshalled document "+type.toString());
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage()+e.getCause().getMessage());
			
		}
		
	}

	private Marshaller getMarshaller() throws JAXBException, PropertyException {
		Marshaller result = storedContext.createMarshaller();
		result.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		if (XSLTHref!=null)
			result.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml-stylesheet type='text/xsl' href='"+XSLTHref+"' ?>");
		
		return result;
	}
	
}

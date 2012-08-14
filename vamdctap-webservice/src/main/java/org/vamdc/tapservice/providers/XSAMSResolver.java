package org.vamdc.tapservice.providers;


import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.vamdc.xsams.io.JAXBContextFactory;
import org.vamdc.xsams.schema.XSAMSData;


//TODO: Check if this class is still necessary

public class XSAMSResolver implements ContextResolver<JAXBContext> {

	private JAXBContext context;
	public final static Class<?>[] supportedCases = {
		XSAMSData.class
	};
	
	public XSAMSResolver() throws JAXBException{
		this.context = JAXBContextFactory.getContext();
	}
	
	public JAXBContext getContext(Class<?> arg0) {
		for (Class<?> supported : supportedCases){
			if (arg0.equals(supported)){
				return context;
			}
		}
		return null;
	}

}

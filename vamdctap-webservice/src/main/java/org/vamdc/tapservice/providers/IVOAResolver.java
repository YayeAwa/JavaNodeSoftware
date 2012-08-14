package org.vamdc.tapservice.providers;


import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;


@Provider
public class IVOAResolver implements ContextResolver<JAXBContext> {

	private JAXBContext context;
	//@SuppressWarnings({ "unchecked" })
	public final static Class<?>[] supportedCases = {
		net.ivoa.xml.vodataservice.v1.ParamHTTP.class,
		net.ivoa.xml.voresource.v1.AccessURL.class,
		net.ivoa.xml.voresource.v1.Capability.class,
		net.ivoa.xml.voresource.v1.Interface.class,
		net.ivoa.xml.vosicapabilities.v1.Capabilities.class,
		org.vamdc.xml.vamdc_tap.v1.VamdcTap.class,
		net.ivoa.xml.vosiavailability.v1.Availability.class
	};
	
	public IVOAResolver() throws JAXBException{
		this.context = JAXBContext.newInstance( supportedCases );
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

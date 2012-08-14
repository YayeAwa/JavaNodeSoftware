package org.vamdc.tapservice.providers;


import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;


//TODO: Check if this class is still necessary
@Provider
public class VOTableResolver implements ContextResolver<JAXBContext> {

	private JAXBContext context;
	public final static Class<?>[] supportedCases = {
		net.ivoa.xml.votable.v1.VOTABLE.class,
		org.vamdc.tapservice.VOTableError.class
	};
	
	public VOTableResolver() throws JAXBException{
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

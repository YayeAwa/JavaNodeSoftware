package org.vamdc.tapservice.providers;

import java.io.IOException;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import net.ivoa.xml.votable.v1.VOTABLE;


@Provider
@Produces("application/x-votable+xml")
public class VOTableMarshaller extends JAXBMarshaller<VOTABLE> {

	public VOTableMarshaller() throws IOException{
		super(VOTABLE.class);
	}

}

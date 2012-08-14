package org.vamdc.tapservice;

import javax.ws.rs.Path;

import net.ivoa.xml.vosiavailability.v1.Availability;
import net.ivoa.xml.vosicapabilities.v1.Capabilities;

import org.vamdc.tapservice.api.VOSIInterface;
import org.vamdc.tapservice.util.AvailabilityMonitor;

import com.sun.jersey.spi.resource.Singleton;



@Singleton
@Path("/VOSI/")
public class VOSIImpl implements VOSIInterface{
	private AvailabilityMonitor mon= AvailabilityMonitor.getMonitor();
	
	@Override
	public Availability getAvailaibility() {
		return VOSIAvailability.getAvailability();
	}

	@Override
	public Capabilities getCapabilities() {
		return VOSICapabilities.get();
	}

	public void setMon(AvailabilityMonitor mon) {
		this.mon = mon;
	}

	public AvailabilityMonitor getMon() {
		return mon;
	}
	


}

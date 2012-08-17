package org.vamdc.tapservice;
import javax.xml.datatype.XMLGregorianCalendar;

import net.ivoa.xml.vosiavailability.v1.Availability;

import org.vamdc.tapservice.util.AvailabilityMonitor;


public class VOSIAvailability {
	
	//Return availability status
	public static Availability getAvailability(){
		Availability myavail = new Availability();
		
		myavail.setAvailable(AvailabilityMonitor.getServiceStatus());
		myavail.setUpSince(AvailabilityMonitor.getUpSince());
		XMLGregorianCalendar downAt = AvailabilityMonitor.getDownAt();
		if (downAt!=null)
			myavail.setDownAt(downAt);
		XMLGregorianCalendar backAt = AvailabilityMonitor.getBackAt();
		if (backAt!=null)
			myavail.setBackAt(backAt);
		myavail.getNote().add(AvailabilityMonitor.getStatusNote());
		
		return myavail;
	}
	
	


}

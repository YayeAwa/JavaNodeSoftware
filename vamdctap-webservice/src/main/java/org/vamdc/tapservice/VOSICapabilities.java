package org.vamdc.tapservice;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import net.ivoa.xml.vodataservice.v1.ParamHTTP;
import net.ivoa.xml.voresource.v1.AccessURL;
import net.ivoa.xml.voresource.v1.Capability;
import net.ivoa.xml.voresource.v1.Interface;
import net.ivoa.xml.vosicapabilities.v1.Capabilities;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.util.DBPlugTalker;
import org.vamdc.tapservice.util.Setting;
import org.vamdc.xml.vamdc_tap.v1.VamdcTap;


public class VOSICapabilities {
	public static Capabilities get(){

		final String baseurl = Setting.baseurls.getValue();

		Collection<String> baseurls = new ArrayList<String>(){private static final long serialVersionUID = 9120067172144791872L;{
			for (String base:baseurl.split("#")){
				try {
					@SuppressWarnings("unused")
					URL service = new URL(base);
					add(base);
				} catch (MalformedURLException e) {}
			}
		}};


		Capabilities returned = new Capabilities();
		VamdcTap mycap = null;
		mycap = (VamdcTap) createCapability(new VamdcTap(),"ivo://vamdc/std/VAMDC-TAP",
				baseurls,"/TAP/");

		//Publish restrictables
		for (Restrictable rest:DBPlugTalker.getRestrictables())
			mycap.getRestrictable().add(rest.name());

		//Publish software versions
		mycap.setVersionOfSoftware("Java VAMDC-TAP implementation 12.07");
		mycap.setVersionOfStandards("12.07");

		//Publish sample queries
		String queries = Setting.test_queries.getValue();
		if (queries!=null && queries.length()>0)
			for (String query:queries.split(";")){
				if (query.length()>1)
					mycap.getSampleQuery().add(query+";");
			}

		returned.getCapability().add(mycap);

		returned.getCapability().add(
				createCapability(new Capability(),"ivo://ivoa.net/std/VOSI#capabilities",
						baseurls,"/VOSI/capabilities"));
		returned.getCapability().add(
				createCapability(new Capability(),"ivo://ivoa.net/std/VOSI#availability",
						baseurls,"/VOSI/availability"));

		return returned;
	};

	private static Capability createCapability(Capability mycap,String standardID, Collection<String> baseURLs, String serviceSuffix){

		mycap.setStandardID(standardID);
		Interface servint = new ParamHTTP();

		for (String serviceURL:baseURLs){
			AccessURL servaddr = new AccessURL();
			servaddr.setUse("base");
			servaddr.setValue(serviceURL+serviceSuffix);
			servint.getAccessURL().add(servaddr);
		}


		mycap.getInterface().add(servint);
		return mycap;
	}

}

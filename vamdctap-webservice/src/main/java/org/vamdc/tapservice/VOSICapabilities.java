package org.vamdc.tapservice;



import java.net.URI;
import java.net.URISyntaxException;
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

		Capabilities returned = new Capabilities();
		Collection<String> baseURLs=splitURLs(Setting.baseurls.getValue(),"#");
		
		VamdcTap mycap = (VamdcTap) createCapability(
				new VamdcTap(),
				"ivo://vamdc/std/VAMDC-TAP",
				baseURLs,
				"/TAP/");

		//Publish restrictables
		for (Restrictable rest:DBPlugTalker.getRestrictables())
			mycap.getRestrictable().add(rest.name());
		
		//Publish returnables from configuration
		String returnables = Setting.returnables.getValue();
		if (returnables!=null && returnables.length()>0)
			for (String kword:returnables.split(";")){
				if (kword.trim().length()>1)
					mycap.getReturnable().add(kword);
			}

		//Publish software versions		
		mycap.setVersionOfSoftware(new Versions().getSWVer());
		mycap.setVersionOfStandards(new Versions().getStdVer());
		
		//Publish processors (applications)
		mycap.getApplication().addAll(
				splitURLs(Setting.processors.getValue(),"#")
				);

		//Publish sample queries
		String queries = Setting.test_queries.getValue();
		if (queries!=null && queries.length()>0)
			for (String query:queries.split(";")){
				if (query.trim().length()>1)
					mycap.getSampleQuery().add(query+";");
			}

		returned.getCapability().add(mycap);

		returned.getCapability().add(
				createCapability(new Capability(),"ivo://ivoa.net/std/VOSI#capabilities",
						baseURLs,"/VOSI/capabilities"));
		returned.getCapability().add(
				createCapability(new Capability(),"ivo://ivoa.net/std/VOSI#availability",
						baseURLs,"/VOSI/availability"));

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
	private static class Versions {
		private String getSWVer(){
			return this.getClass().getPackage().getImplementationTitle();
		}
		
		private String getStdVer(){
			return this.getClass().getPackage().getImplementationVersion().substring(0,5);
			
		}
		
	}
	
	private static Collection<String> splitURLs(final String listString,final String splitter){
		Collection<String> urls = new ArrayList<String>(){
				private static final long serialVersionUID = 9120067172144791872L;
				{
					for (String base:listString.split(splitter)){
						try {
							URI service = new URI(base);
							add(service.toString());
						}catch (URISyntaxException e){
							
						}
					}
				}
			};
		return urls;
	}

}

package org.vamdc.tapservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.vamdc.tapservice.util.Settings;



@Path("/config")
public class ConfigStatus {
	/**
	 * @return current service configuration
	 */
	@GET
	@Produces("text/plain")
	public String getStatus(){
		String result ="";
		result = "#Default configuration parameters\n";
		for (String key:Settings.defaultConfiguration.keySet()){
			result=result.concat(key+"="+Settings.defaultConfiguration.get(key)+"\n");
		}
		if (Settings.isConfigured()){	
			result = result.concat( "\n#Configured parameters\n");
			for (String key:Settings.getKeys()){
				result=result.concat(key+"="+Settings.getValue(key)+"\n");
			}
		}
		return result;
	}
}

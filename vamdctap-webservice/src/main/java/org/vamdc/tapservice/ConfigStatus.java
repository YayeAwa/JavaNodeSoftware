package org.vamdc.tapservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.vamdc.tapservice.util.Setting;




@Path("/config")
public class ConfigStatus {
	/**
	 * @return current service configuration
	 */
	@GET
	@Produces("text/plain")
	public String getStatus(){
		StringBuilder result = new StringBuilder();
		result.append("#Default configuration parameters\n");
		for (Setting option:Setting.values()){
			result.append(option.getKey()).append("=").append(option.getDefault()).append("\n");
		}
		if (Setting.isConfigured()){
			result.append("\n#Configured parameters\n");
			for (Setting option:Setting.values()){
				result.append(option.getKey()).append("=").append(option.getValue()).append("\n");
			}
		}
		return result.toString();
	}
}

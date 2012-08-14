package org.vamdc.tapservice.util;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/*
 * Filter that upcases all queryParameter names, so GET requests become case-insensitive
 */
public class ParamsCaseFilter implements ContainerRequestFilter {

	private Logger logger;
	public ParamsCaseFilter(){
		logger = LoggerFactory.getLogger("org.vamdc.tapservice");
		logger.debug("Initializing "+this.getClass().toString()+" filter class");
	}

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		MultivaluedMap<String, String> params = request.getQueryParameters();
		Collection<String> keys = new HashSet<String>(params.keySet());
		Boolean changed = false;
		//Upcase all parameter names
		for (String param:keys){
			logger.debug("Checking parameter "+param);
			String paramUC = param.toUpperCase();
			if (!paramUC.equals(param)){
				logger.debug("put"+params.get(param).toString()+"for parameter"+param);
				if (params.containsKey(paramUC)){
					for(String value:params.get(param))
						params.add(paramUC, value);
				}else
					params.put(paramUC,params.get(param));
				params.remove(param);
				changed=true;
			}
		}
		
		
		//Re-build query with new parameters if changed any of them
		if (changed){
			UriBuilder requestURIBuilder = UriBuilder.fromUri(request.getRequestUri()).replaceQuery("");
			for (String param:params.keySet()){
				for (String value:params.get(param)){
					logger.debug("Add value"+value+"for parameter"+param);
					requestURIBuilder = requestURIBuilder.queryParam(param, value);
				};
			}
			URI requestUri = requestURIBuilder.build();
			logger.debug("result URI "+requestUri.toString());
			request.setUris(request.getBaseUri(), requestUri);
		};
		return request;
	}

}

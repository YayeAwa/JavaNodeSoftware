package org.vamdc.tapservice.api;

import java.util.Collection;
import java.util.Map;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.dictionary.Restrictable;



/**
 * Interface a database plugin must implement to work with the Java VAMDC-TAP node software implementation.
 * @author doronin
 *
 */
public interface DatabasePlugin {

	/**
	 * Get a collection of Restrictable keywords supported by this node.<br>
	 * Must be a subset of the VAMDC dictionary:<br>
	 * http://dictionary.vamdc.org/
	 * 
	 * @return a collection of Restrictable keywords from VAMDC dictionary
	 */
	public abstract Collection<Restrictable> getRestrictables();
	
	/**
	 * Build XSAMS data from user request.
	 * Methods of RequestInterface XSAMSManager should be called internally to construct the XSAMS tree.
	 * 
	 * 
	 * 
	 * @param userRequest request and response wrapper, contains user query object and XSAMS output wrapper 
	 */
	public abstract void buildXSAMS (RequestInterface userRequest);
	
	
	/**
	 * Get query metrics. Is called when there is a HEAD request to a node,
	 * result should be a map of HTTP header names and their values.
	 * In case of an HTTP GET request this metrics are not used currently but
	 * may become used in future versions
	 * 
	 * @param userRequest the same RequestInterface structure as for buildXSAMS.
	 * @return map of HeaderMetrics and their values. Omitted headers would not be outputed.
	 * 
	 */
	public abstract Map<HeaderMetrics,Object> getMetrics(RequestInterface userRequest);
	
	/**
	 * Do some really node-specific availability checks
	 * @return true if node is available
	 */
	public abstract boolean isAvailable();

	
	/**
	 * Return verbose reason of error if node is not available
	 * @return error message
	 */
	public abstract String getErrorMessage();
}

package org.vamdc.tapservice.api;

import java.util.Collection;
import java.util.Date;

import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.vamdc.dictionary.Requestable;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.Query;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.xsams.XSAMSManager;

/**
 * Interface that VAMDC-TAP node software implements to communicate with database plugin
 * @author doronin
 *
 */
public interface RequestInterface {

	/**
	 * Get XSAMSData wrapper.
	 * It contains several helper methods, and all created XSAMS branches should be attached to it.
	 * @return XSAMSData wrapper
	 */
	public abstract XSAMSManager getXsamsManager();

	/**
	 * Get Apache Cayenne object context, if you want to talk to database using Apache Cayenne
	 * @return Apache Cayenne object context
	 */
	public abstract ObjectContext getCayenneContext();

	/**
	 * Get list of RestrictExpression from query, if one wants to process query in dummy AND'ed list mode
	 * @return list of RestrictExpression from query
	 */
	public abstract Collection<RestrictExpression> getRestricts();

	/**
	 * Get normalized logic tree from query, used if one wants to process query in smart way.
	 * <br>In the tree only nodes passed through 
	 * @see org.vamdc.tapservice.api.DatabasePlugin#getRestrictables() 
	 *  are left, others are omitted (as if they are always resulting in TRUE expression). 
	 * @return root element of restricts tree
	 */
	public abstract LogicNode getRestrictsTree();

	/**
	 * Returns true if request is valid and should be processed
	 * @return request validity
	 */
	public abstract boolean isValid();
	
	/**
	 * Get query object itself
	 */
	public abstract Query getQuery();

	/**
	 * Get logger
	 * @return slf4j logger
	 */
	public abstract Logger getLogger(Class<?> classname);

	/**
	 * Get query string, useful for logging purposes
	 * @return input query string
	 */
	public abstract String getQueryString();

	/**
	 * Check if we are requested to build certain part of XSAMS
	 * @param branch element from @see org.vamdc.dictionary.Requestable
	 * @return true if that part is requested
	 */
	public abstract boolean checkBranch(Requestable branch);
	
	/**
	 * Set the date corresponding to the last-modified date response header
	 * @param date java.util.Date reflecting the newest response record date
	 */
	public abstract void setLastModified(Date date); 

}
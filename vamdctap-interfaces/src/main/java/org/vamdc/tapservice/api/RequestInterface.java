package org.vamdc.tapservice.api;

import java.util.Collection;
import java.util.Date;

import org.apache.cayenne.ObjectContext;
import org.vamdc.dictionary.Requestable;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.Query;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.xsams.XSAMSManager;

/**
 * Interface implemented by the VAMDC-TAP node software to communicate with the database plugin.
 * @author doronin
 *
 */
public interface RequestInterface {

	/**
	 * Get the XSAMS manager, use it to attach the constructed XSAMS document branches.
	 * @return the object from XSAMS-extra library, implementing the XSAMS document manager interface
	 */
	public abstract XSAMSManager getXsamsManager();

	/**
	 * Get the Apache Cayenne object context, used to talk to a database using the Apache Cayenne library
	 * @return Apache Cayenne object context
	 */
	public abstract ObjectContext getCayenneContext();

	/**
	 * Get a collection of used RestrictExpression keys from the query.
	 * Enables to process the query in a dummy mode.
	 * Should NOT be used for the query processing, since the information on the query logic is lost.
	 * Use get
	 * @return a collection of RestrictExpression keywords used in the incoming query
	 */
	public abstract Collection<RestrictExpression> getQueryKeywords();

	/**
	 * Get the normalized logic tree from the query.
	 * This tree is a starting point to process query in smart way.
	 * A mapper may be applied on the query to obtain the Apache Cayenne Expression object,
	 * @see org.vamdc.tapservice.querymapper.QueryMapper and the Java Node Software documentation for more info.
	 * 
	 * @return the root element of the incoming query logical tree
	 */
	public abstract LogicNode getQueryTree();

	/**
	 * Returns true if request is valid and should be processed
	 * @return request validity
	 */
	public abstract boolean isValid();
	
	/**
	 * Get the query object as defined by the @see org.vamdc.tapservice.vss2.Query interface.
	 */
	public abstract Query getQuery();

	/**
	 * Get the incoming query string, may be used for logging purposes
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
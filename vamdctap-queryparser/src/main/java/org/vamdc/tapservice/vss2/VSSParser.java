package org.vamdc.tapservice.vss2;

import java.util.Collection;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.impl4.QueryImpl4;

/**
 * Factory class for queryparser library
 * @author doronin
 *
 */
public class VSSParser {

	/**
	 * Parse a given VSS2 query, recognize all the supported Restrictable keywords from the Dictionary library.
	 * @param query VSS2 query string to parse
	 * @return An object implementing the Query interface, providing access to a tree of LogicNode objects corresponding to the provided query logic
	 */
	public static Query parse(String query){
		return new QueryImpl4(query);
	}
	
	/**
	 * Parse a given VSS2 query, while applying the filter of supported Restrictable keywords
	 * @param query VSS2 query string
	 * @param filter A collection of Restrictable
	 * @return An object implementing the Query interface, providing access to a tree of LogicNode objects corresponding to the provided query logic
	 */
	public static Query parse(String query,Collection<Restrictable> filter){
		return new QueryImpl4(query,filter);
	}
	
	/**
	 * "Deprecated - Use the SLF4J logger to see the debug log"
	 * @param query
	 * @param filter
	 * @return
	 */
	@Deprecated
	public static Query parseDebug(String query){
		return new QueryImpl4(query,null,true);
	}
	
	/**
	 * "Deprecated - Use the SLF4J logger to see the debug log"
	 * @param query
	 * @param filter
	 * @return
	 */
	@Deprecated
	static Query parseDebug(String query,Collection<Restrictable> filter){
		return new QueryImpl4(query,filter,true);
		
	}
	
}

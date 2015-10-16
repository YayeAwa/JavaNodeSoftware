package org.vamdc.tapservice.vss2.impl4;

import java.util.Collection;
import java.util.EnumSet;

import org.vamdc.dictionary.Factory;
import org.vamdc.dictionary.Requestable;
import org.vamdc.dictionary.RequestableLogic;

/**
 * Check if the branch is requested
 * @author doronin
 *
 */
public class RequestableHandler {
	
	/**
	 * Columns really present in a query 
	 */
	private Collection<Requestable> queryRequestables = EnumSet.noneOf(Requestable.class);
	
	private RequestableLogic logic = Factory.getRequestableLogic();
	
	void add(String key){
		if (!(key.contains("*")|| key.equalsIgnoreCase("all")))
		queryRequestables.add(Requestable.valueOfIgnoreCase(key));
	}
	
	public boolean checkBranch(Requestable keyword){
		return logic.isEnabled(queryRequestables, keyword);
	}
	
	Collection<Requestable> getQueryRequestables(){
		return queryRequestables;
	}
	
}

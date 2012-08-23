package org.vamdc.tapservice.vss2.impl;

import java.util.Collection;
import java.util.EnumSet;


import org.antlr.runtime.tree.CommonTree;
import org.vamdc.dictionary.Factory;
import org.vamdc.dictionary.Requestable;
import org.vamdc.dictionary.RequestableLogic;

/**
 * Check if the branch is requested
 * @author doronin
 *
 */
public class RequestableLoader {
	
	/**
	 * Columns really present in a query 
	 */
	private Collection<Requestable> queryRequestables;
	
	private RequestableLogic logic;
	
	/**
	 * Constructor, accepting query SELECT subtree
	 * @param selectBranch
	 */
	public RequestableLoader( CommonTree selectBranch){
		logic = Factory.getRequestableLogic();
		queryRequestables = this.findColumnNames(selectBranch);
		
	}
	
	public boolean checkBranch(Requestable keyword){
		return logic.isEnabled(queryRequestables, keyword);
	}
	
	Collection<Requestable> getQueryRequestables(){
		return queryRequestables;
	}
		
	/**
	 * Find all column names in tree
	 * @param basetree subtree representing SELECT columns
	 * @return list of requested columns.
	 */
	private Collection<Requestable> findColumnNames(CommonTree basetree){
		EnumSet<Requestable> columns = EnumSet.noneOf(Requestable.class);
		String[] words = {"COLUMN_EXPRESSION"};
		
		Collection<CommonTree> mylist = CommonTreeTools.findAllMatches(basetree,words);

		for (CommonTree column:mylist) {
			String in = column.getChild(0).getText();
			if (!(in.contains("*")|| in.equalsIgnoreCase("all")))
					columns.add(Requestable.valueOfIgnoreCase(in));
		}

		return columns;
	}
	
}

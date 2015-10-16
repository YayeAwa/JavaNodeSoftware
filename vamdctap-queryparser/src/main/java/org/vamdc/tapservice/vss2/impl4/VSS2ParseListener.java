package org.vamdc.tapservice.vss2.impl4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.tapservice.vss2.impl.PrefixHandler;
import org.vamdc.tapservice.vss2.impl.RequestableLoader;
import org.vamdc.tapservice.vsssqlparser.VSS2BaseListener;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser;


/**
 * Listener extension implementing the logic of VSS2 query parsing.
 * @author doronin
 *
 */
class VSS2ParseListener extends VSS2BaseListener{
	
	//The most dummy way of restricting - a dummy list of keywords
	protected List<RestrictExpression> restrictsList = new ArrayList<RestrictExpression>();
	//The tree of restricts - for sophisticated query handling
	protected LogicNode logicTree;
	//A filter for the Selection keywords - use only restrictables from this list.
	protected Collection<Restrictable> allowedRestrictables;
	//Branches and their dependencies
	protected RequestableHandler requestables = new RequestableHandler();
	//Prefixes handler
	protected PrefixHandler prefixes;
	
	protected boolean debug=false;
	
	VSS2ParseListener(){
		
		
	}
	
	/**
	 * Called on the end of the select column
	 */
	@Override 
	public void exitResult_column(VSS2Parser.Result_columnContext ctx) { 
		requestables.add(ctx.getText());
		System.out.println(ctx.getText());
	}
	

}

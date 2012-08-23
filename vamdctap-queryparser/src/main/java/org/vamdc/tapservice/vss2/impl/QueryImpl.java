package org.vamdc.tapservice.vss2.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.vamdc.dictionary.Requestable;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.dictionary.VSSPrefix;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.NodeFilter;
import org.vamdc.tapservice.vss2.Prefix;
import org.vamdc.tapservice.vss2.Query;
import org.vamdc.tapservice.vss2.LogicNode.Operator;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.tapservice.vsssqlparser.CommonTreeDumper;
import org.vamdc.tapservice.vsssqlparser.Sql_Lexer;
import org.vamdc.tapservice.vsssqlparser.Sql_Parser;


/**
 * VSS2 query main interface implementation
 * @author doronin
 *
 */
public class QueryImpl implements Query{

	//Most dummy way of restricting - dummy list of keywords
	private List<RestrictExpression> restrictsList = new ArrayList<RestrictExpression>();
	//Original query string
	private String query;
	//Tree of restricts - for sophisticated query handling
	private LogicNode logicTree;
	//Selection keywords filter - use only restrictables from this list.
	private Collection<Restrictable> restrictsFilter;
	//Handler for branches selector and their dependencies
	private RequestableLoader selectBranches;
	//Handler for prefixes
	private PrefixHandler prefixes;
	
	private boolean debug=false;
	
	public QueryImpl(String query){
		this.debug=false;
		init (query, null);
	}
	
	public QueryImpl(String query,Collection<Restrictable> filter){
		this.debug=false;
		init(query, filter);
	}
	
	public QueryImpl(String query,Collection<Restrictable> filter, boolean debug){
		this.debug=debug;
		init(query,filter);
	}

	private void init(String query, Collection<Restrictable> filter) {
		this.restrictsFilter = filter;
		this.query = query;
		if (this.query !=null) {
			parseQuery();
		}
	}
	

	
	private void parseQuery(){
		
		CommonTree parsedQuery = parseString(query);
		debugTree(parsedQuery);
		
		loadSelectPart(parsedQuery);
		
		loadWherePart(parsedQuery);
		
		verifyQueryIsSensible();
		
		prefixes = new PrefixHandler(this.restrictsList);
	}

	private void verifyQueryIsSensible() {
		if (restrictsList==null|| (restrictsList.size()==0 && selectBranches.getQueryRequestables().size()==0))
			throw new IllegalArgumentException("Query does not limit anything");
	}

	private void loadWherePart(CommonTree parsedQuery) {
		CommonTree whereRoot = CommonTreeTools.findWhere(parsedQuery);
		if (whereRoot!=null && whereRoot.getChildCount()>0)
			this.logicTree = findExpr((CommonTree) CommonTreeTools.findWhere(parsedQuery).getChild(0));
	}

	private void loadSelectPart(CommonTree parsedQuery) {
		CommonTree selectPart = CommonTreeTools.findSelectColumns(parsedQuery);
		if (selectPart==null)
			throw new IllegalArgumentException("Select part was not found");
		this.selectBranches = new RequestableLoader(selectPart);
	}

	private void debugTree(CommonTree parsedQuery) {
		if (this.debug){
			System.out.println("Tree:");
			System.out.println( CommonTreeDumper.toString(parsedQuery));
		}
	}
	
	/**
	 * Parse the query into tree
	 * @param query
	 * @return result CommonTree
	 */
	private static CommonTree parseString(String query){
		Sql_Lexer lexer = new Sql_Lexer(new ANTLRStringStream(query));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Sql_Parser parser = new Sql_Parser(tokens);
		CommonTree mytree=null;
		try {
			mytree = (CommonTree) parser.sql_stmt().getTree();
		}catch (RecognitionException e) {
		};
		return mytree;
	}
	
	/**
	 * get a tree with comparison expressions from subtree of WHERE clause:
	 * @param baseTree tree where to look for expressions
	 * @return root of the expressions tree
	 */
	private LogicNode findExpr(CommonTree baseTree){

		
		if (baseTree!=null && baseTree.getText()!=null){
			//Check if we have logical expression
			if ( baseTree.getText().equalsIgnoreCase("AND") || baseTree.getText().equalsIgnoreCase("OR") || baseTree.getText().equalsIgnoreCase("NOT")|| baseTree.getText().equalsIgnoreCase("WHERE")){
				//Create node
				LogicNodeImpl thisnode = new LogicNodeImpl(Operator.valueOf(baseTree.getText().toUpperCase()));
				//Fill children
				for (int i = 0; i < baseTree.getChildCount(); i++) {			
					LogicNode sub = findExpr((CommonTree)baseTree.getChild(i));
					if (sub!=null){
						//Try to float child elements up if the child is a logic node with the same operator as it's parent
						if ((thisnode.getOperator()==Operator.AND || thisnode.getOperator()==Operator.OR )&& sub.getOperator() == thisnode.getOperator())
							for (Object child:sub.getValues()){
								thisnode.addChild((LogicNode)child);
							}
						else
							thisnode.addChild(sub);//Add expression only if it is not null
					}
				}
				
				//Verify that I'm ok
				int childCount=thisnode.getValues().size();
				if (childCount>1 || (childCount==1 && thisnode.getOperator().equals(Operator.NOT)))
					return thisnode;
				else if (childCount==1)
					return thisnode.getValues().iterator().next();
				else return null;
			}else {
				RestrictExpressionImpl expr = new RestrictExpressionImpl(baseTree,restrictsFilter);
				if (expr.isValid()){//If expression is valid, add it
					restrictsList.add(expr);
					return expr;
				}
				return null;
			}
		}
		return null;
	}
	

	
	
	@Override
	public boolean checkSelectBranch(Requestable branch) {
		return selectBranches.checkBranch(branch);
	}

	@Override
	public LogicNode getFilteredTree(Collection<Restrictable> allowedKeywords) {
		return NodeFilter.filterKeywords(logicTree, allowedKeywords);
	}

	@Override
	public LogicNode getPrefixedTree(VSSPrefix prefix, int index) {
		return NodeFilter.filterPrefix(logicTree, new Prefix(prefix,index));
	}

	@Override
	public Collection<Prefix> getPrefixes() {
		return prefixes.getPrefixes();
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public List<RestrictExpression> getRestrictsList() {
		return restrictsList;
	}

	@Override
	public LogicNode getRestrictsTree() {
		return logicTree;
	}

}

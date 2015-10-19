package org.vamdc.tapservice.vss2.impl4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.tapservice.vss2.LogicNode.Operator;
import org.vamdc.tapservice.vsssqlparser.VSS2BaseListener;
import org.vamdc.tapservice.vsssqlparser.VSS2Lexer;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser.Column_nameContext;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser.Table_nameContext;


/**
 * Listener extension implementing the walker of the VSS2 tree.
 * @author doronin
 *
 */
class VSS2ParseListener extends VSS2BaseListener{
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	//The most dummy way of restricting - a dummy list of keywords
	protected List<RestrictExpression> restrictsList = new ArrayList<RestrictExpression>();
	//The tree of restricts - for sophisticated query handling
	protected LogicNode logicTree;
	//A filter for the Selection keywords - use only restrictables from this list.
	protected Collection<Restrictable> allowedRestrictables;
	//Branches and their dependencies
	protected RequestableHandler requestables = new RequestableHandler();
	//Prefixes handler
	protected PrefixHandler4 prefixes;
		
	protected String query="";
	
	protected VSS2Parser parser;
	
	enum internalOps{
		dot,
		OPEN_PARENTH,
		CLOSE_PARENTH
	}
	
	
	@Override public void enterSelect_where(VSS2Parser.Select_whereContext ctx) {
		Object tree = parseWhereTree(ctx.getChild(0));
		if (tree!=null){
			logger.debug("Logic tree of the where part {}",(this.logicTree!=null) ?this.logicTree : "null");
			logger.trace("The root node class is {}",tree.getClass().toString());
		}
		if (tree instanceof LogicNode){
			this.logicTree=(LogicNode) tree;
		}
			
	}
	
	/**
	 * Recursive method to process the Where part of the request tree
	 * @param ctx ParseTree object
	 * @return a translated Java object
	 */
	private Object parseWhereTree(ParseTree ctx){
		if (ctx==null) {
			logger.debug("parseWhereTree called with a null argument, weird!");
			return null;
		}
		
		logger.trace("cc{}, root {}, payload {}",ctx.getChildCount(),ctx.getClass(),ctx.getPayload().getClass());

		if (ctx!=null && ctx.getPayload() instanceof CommonToken)
		{//The leaf node having a token payload
			return processLeaf(ctx);
		}
		if (ctx.getChildCount()>1)
		{//The node with branching children
			return processBranching(ctx);	
		}else if (ctx.getChildCount()==1)
		{//The node with a single child
			return processSingleChild(ctx);
		}
		return null;
	}

	private Object processLeaf(ParseTree ctx) {
		CommonToken ct = (CommonToken)ctx.getPayload();
		int ctt=ct.getType();
		logger.trace("Leaf token {}",ctt);
		if (ctt==VSS2Lexer.K_OR || ctt==VSS2Lexer.K_AND || ctt==VSS2Lexer.K_NOT){
			logger.trace("logic ({})",ct.getText());
			return Operator.valueOf(ct.getText().toUpperCase());
		}else if (RestrictExpression4.supportsOperation(ctt)){
			logger.trace("leaf ({})",ct.getText());
			return RestrictExpression4.ExprMap.get(ctt);
		}else if (ctt==VSS2Lexer.STRING_LITERAL||ctt==VSS2Lexer.IDENTIFIER){
			logger.trace("String {}",ct.getText());
			return ct.getText().replace("'", "").replace("\"", "");
		}else if (ctt==VSS2Lexer.INTEGER_LITERAL){
			logger.trace("Integer {}",ct.getText());
			return Integer.valueOf(ct.getText());
		}else if (ctt==VSS2Lexer.FLOAT_LITERAL){
			logger.trace("Double {}",ct.getText());
			return Double.valueOf(ct.getText());
		}else if (ctt==VSS2Lexer.DOT){
			logger.trace("Dot");
			return internalOps.dot;
		}else if (ctt==VSS2Lexer.OPEN_PAR){
			logger.trace("Open parentheses");
			return internalOps.OPEN_PARENTH;
		}else if (ctt==VSS2Lexer.CLOSE_PAR){
			logger.trace("Close parentheses");
			return internalOps.CLOSE_PARENTH;
		}else{
			logger.warn("Unhandled leaf node {} {} for the query {}",ctx.getText(),ctx.getClass(),this.query);
			return null;
		}
			
	}

	private Object processBranching(ParseTree ctx) {
		ArrayList<Object> children = new ArrayList<Object>();
		for (int i=0;i<ctx.getChildCount();i++){
			children.add(parseWhereTree(ctx.getChild(i)));
		}
		
		//Extra braces workaround
		if (children.size()==3 && internalOps.OPEN_PARENTH == children.get(0)&& internalOps.CLOSE_PARENTH==children.get(2)){
			logger.trace("braces");
			return children.get(1);
		}
		
		
		for (Object child:children){
			if (child instanceof Restrictable || 
					(child instanceof Operator
							&&RestrictExpression4.supportsOperatior((Operator)child))){//We have a RestrictExpression around
				//Build a normal RestrictExpression here
				RestrictExpression re = new RestrictExpression4(children);
				logger.debug("RestrExpr {}",re);
				this.restrictsList.add(re);
				return re;
			}else if (Operator.AND.equals(child)||Operator.OR.equals(child)||Operator.NOT.equals(child)){
				//Here we are at the logicnode (AND|OR|NOT) level
				LogicNode ln = new LogicNode4(children);
				logger.debug("LogicNode {}",ln);
				return ln;
			}else if (child instanceof internalOps && internalOps.dot.equals(child)){
				//Looks like a restrictable with prefixes
				RestrictExpression re = new RestrictExpression4(children,true);
				logger.debug("PrefixedR  {}",re);
				//Do not add this RE to restrictsList, new RestrictExpression will be constructed out of it later.
				return re;
			}
		}
		
		logger.warn("Unhandled branching at node {} {}",ctx.getText(),ctx.getClass());
		return null;
	}
	
	private Object processSingleChild(ParseTree ctx) {
		if (ctx instanceof Table_nameContext){
			String ret = (String)parseWhereTree(ctx.getChild(0));
			logger.trace("Prefix {}",ret);
			return ret;
		}else if (ctx instanceof Column_nameContext)
		{
			Restrictable ret = Restrictable.valueOfIgnoreCase((String)parseWhereTree(ctx.getChild(0)));
			if (this.allowedRestrictables==null
					|| this.allowedRestrictables.size()==0
					|| this.allowedRestrictables.contains(ret))
			{
				logger.trace("Restrictable {}",ret);
				return ret;
			}else
			{
				throw new IllegalArgumentException("The keyword "+ret.toString()+" is not in the list of allowed keywords");
			}
		}
		return parseWhereTree(ctx.getChild(0));
	}

	

	

	/**
	 * Called on the end of the select XX column names
	 */
	@Override 
	public void exitResult_column(VSS2Parser.Result_columnContext ctx) { 
		requestables.add(ctx.getText());
		logger.debug("Requested {}",ctx.getText());
	}
	

}

package org.vamdc.tapservice.vss2.impl4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.tapservice.vss2.LogicNode.Operator;
import org.vamdc.tapservice.vss2.impl.LogicNodeImpl;
import org.vamdc.tapservice.vss2.impl.PrefixHandler;
import org.vamdc.tapservice.vsssqlparser.VSS2BaseListener;
import org.vamdc.tapservice.vsssqlparser.VSS2Lexer;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser.ExprContext;


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
	
	protected String query="";
	
	protected VSS2Parser parser;
	
	VSS2ParseListener(){
	}
	
	
	@Override public void enterSelect_where(VSS2Parser.Select_whereContext ctx) {
		this.logicTree = parseTree(ctx.getChild(0));
		//System.out.println(" "+ctx.getRuleIndex()+" "+ctx.getChildCount()+ctx.getText());
		//System.out.println(ctx.toStringTree());
	}
	
	private LogicNode parseTree(ParseTree ctx) {
		System.out.println("cc"+ctx.getChildCount()+" plc "+ctx.getPayload().getClass()+" nc "+ctx.getClass());
		if (ctx!=null && ctx.getPayload() instanceof CommonToken)
		{
			CommonToken ct = (CommonToken)ctx.getPayload();
			int ctt=ct.getType();
				
			if (ctt==VSS2Lexer.K_OR || ctt==VSS2Lexer.K_AND || ctt==VSS2Lexer.K_NOT){
				return makeLogicNodeImpl(ctx);
			}else if (RestrictExpression4.supportsOperation(ctt)){
			    return new RestrictExpression4(ctx,this.allowedRestrictables);
			}else{
				System.out.println("missed token "+ct.getType()+" tt "+ct.getText()+" cc "+ctx.getChildCount()+" pcc "+ctx.getParent().getChildCount());
			}
		}
		
		if (ctx instanceof ExprContext){
			if (ctx.getChildCount()>1){
				ArrayList<LogicNode> children = new ArrayList<LogicNode>();
				for (int i=0;i<ctx.getChildCount();i++){
					LogicNode res=parseTree(ctx.getChild(i));
					
					if (res instanceof LogicNodeImpl){
						return res;
					}
					if (res!=null){
						children.add(res);
						System.out.println("exprcontext children "+res.getClass());
					}
				}
				if (children.size()==1)
					return children.get(0);
				System.out.println("return null 1");
				return null;
				//No logic in this expr, but many children, strange...
				//throw new IllegalArgumentException("Strange query"+this.query);
			}
		}
		
		/*
		//if (baseTree!=null && baseTree.getText()!=null){
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
		*/
		
		//Parse the child if we got one and do not know what else to do
		if (ctx.getChildCount()==1)
			return parseTree(ctx.getChild(0));

		System.out.println("return null 2");
		return null;
	}


	private LogicNode makeLogicNodeImpl(ParseTree ctx) {
		CommonToken ct=(CommonToken)ctx.getPayload();
		if (this.debug) System.out.println("MLNToken "+ct.getType()+" "+ct.getText());
		LogicNodeImpl thisnode = new LogicNodeImpl(Operator.valueOf(ct.getText().toUpperCase()));
		ParseTree parent=ctx.getParent();
		for (int i=0;i<parent.getChildCount();i++){
			ParseTree child=parent.getChild(i);
			if (child!=ctx){
				if (this.debug) System.out.println("child "+i+" "+child.getPayload().getClass());
				LogicNode newChild = parseTree(child);
				//if (this.debug && newChild!=null)
				System.out.println("newchild "+newChild);
				thisnode.addChild(newChild);
			}
		}
		
		//Verify that I'm ok
		int childCount=thisnode.getValues().size();
		if (childCount>1 || (childCount==1 && thisnode.getOperator().equals(Operator.NOT)))
			return thisnode;
		else if (childCount==1)
			return thisnode.getValues().iterator().next();
		else return null;
	}


	/**
	 * Called on the end of the select XX column names
	 */
	@Override 
	public void exitResult_column(VSS2Parser.Result_columnContext ctx) { 
		requestables.add(ctx.getText());
		if (this.debug) System.out.println("Requested "+ctx.getText());
	}
	

}

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
		this.logicTree = verifyTree(ctx.getChild(0));
		System.out.println((this.logicTree!=null) ?this.logicTree : "null");
		//System.out.println(" "+ctx.getRuleIndex()+" "+ctx.getChildCount()+ctx.getText());
		//System.out.println(ctx.toStringTree());
	}
	
	private LogicNode verifyTree(ParseTree ctx){
		Object result = reParseTree(ctx);
		System.out.println(result.getClass().toString());
		if (result instanceof LogicNode)
			return (LogicNode) result;
		return null;
	}
	
	private Object reParseTree(ParseTree ctx){
		
		System.out.println("cc"+ctx.getChildCount()+" plc "+ctx.getPayload().getClass()+" nc "+ctx.getClass());
	
		if (ctx!=null && ctx.getPayload() instanceof CommonToken)
		{
			return processLeaves(ctx);
		}
		if (ctx.getChildCount()>1){
			ArrayList<Object> children = new ArrayList<Object>();
			for (int i=0;i<ctx.getChildCount();i++){
				children.add(reParseTree(ctx.getChild(i)));
			}
			for (Object child:children){
				if (child instanceof Restrictable){
					RestrictExpression re = new RestrictExpression4(children);
					this.restrictsList.add(re);
					System.out.println(re);
					return re;
				}else if (Operator.AND.equals(child)||Operator.OR.equals(child)||Operator.NOT.equals(child)){
					LogicNode ln = new LogicNode4(children);
					System.out.println(ln);
					return ln;
				}
			}
			
		}
		if (ctx.getChildCount()==1){
			return reParseTree(ctx.getChild(0));
		}
		return null;
	}

	private Object processLeaves(ParseTree ctx) {
		CommonToken ct = (CommonToken)ctx.getPayload();
		int ctt=ct.getType();
		System.out.println("type"+ctt);
		if (ctt==VSS2Lexer.K_OR || ctt==VSS2Lexer.K_AND || ctt==VSS2Lexer.K_NOT){
			System.out.println("ctln("+ct.getText()+")");
			return Operator.valueOf(ct.getText().toUpperCase());
		}else if (RestrictExpression4.supportsOperation(ctt)){
			System.out.println("ctre("+ct.getText()+")");
			return RestrictExpression4.ExprMap.get(ctt);
		}else if (ctt==VSS2Lexer.STRING_LITERAL){
			return ct.getText().replace("'", "").replace("\"", "");
		}else if (ctt==VSS2Lexer.INTEGER_LITERAL){
			return Integer.valueOf(ct.getText());
		}else if (ctt==VSS2Lexer.FLOAT_LITERAL){
			return Double.valueOf(ct.getText());
		}else if (ctt==VSS2Lexer.IDENTIFIER){
			return Restrictable.valueOfIgnoreCase(ct.getText());
		}else{
			System.out.println("Nothing to return!"+ctx.toStringTree(parser));
			return null;
		}
			
	}
	
	private LogicNode parseTree(ParseTree ctx) {
		//System.out.println("cc"+ctx.getChildCount()+" plc "+ctx.getPayload().getClass()+" nc "+ctx.getClass());
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
						System.out.println("Logic node!"+res);
						//return res;
					}
					if (res!=null){
						children.add(res);
						System.out.println("exprcontext children "+res);
					}
				}
				if (children.size()>=1)
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
				if (newChild!=null){
					//if (this.debug && newChild!=null)
					System.out.println("newchild "+newChild);
					if (newChild instanceof LogicNodeImpl && newChild.getOperator()==thisnode.getOperator())
						for (LogicNode subchild:((LogicNodeImpl)newChild).getValues())
							thisnode.addChild(subchild);
					else
						thisnode.addChild(newChild);
				}
			}
		}
		
		//Verify that I'm ok
		int childCount=thisnode.getValues().size();
		if (childCount>1 || (childCount==1 && thisnode.getOperator().equals(Operator.NOT)))
			return thisnode;
		else if (childCount==1)
			return thisnode.getValues().iterator().next();
		System.out.println("return null 3");
		return null;
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

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
import org.vamdc.tapservice.vsssqlparser.VSS2Parser.Column_nameContext;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser.ExprContext;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser.Table_nameContext;


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
	protected PrefixHandler4 prefixes;
	
	protected boolean debug=false;
	
	protected String query="";
	
	protected VSS2Parser parser;
	
	VSS2ParseListener(){
	}
	
	enum internalOps{
		dot,
		lbrace,
		rbrace
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
				if (child instanceof Restrictable || 
						(child instanceof Operator
								&&RestrictExpression4.supportsOperatior((Operator)child))){//We have a RestrictExpression around
					//Build a normal RestrictExpression here
					RestrictExpression re = new RestrictExpression4(children);
					this.restrictsList.add(re);
					System.out.println(re);
					return re;
				}else if (Operator.AND.equals(child)||Operator.OR.equals(child)||Operator.NOT.equals(child)){
					//Here we are at the logicnode (AND|OR|NOT) level
					LogicNode ln = new LogicNode4(children);
					System.out.println(ln);
					return ln;
				}else if (child instanceof internalOps && internalOps.dot.equals(child)){
					//Looks like a restrictable with prefixes
					RestrictExpression re = new RestrictExpression4(children,true);
					System.out.println("prefixed "+re);
					return re;
				}
			}
			
		}
		if (ctx.getChildCount()==1){
			if (ctx instanceof Table_nameContext){
				return (String)reParseTree(ctx.getChild(0));
			}else if (ctx instanceof Column_nameContext){
				Restrictable ret = Restrictable.valueOfIgnoreCase((String)reParseTree(ctx.getChild(0)));
				if (this.allowedRestrictables==null
						|| this.allowedRestrictables.size()==0
						|| this.allowedRestrictables.contains(ret))
					return ret;
				else
					throw new IllegalArgumentException("The keyword "+ret.toString()+" is not in the list of allowed keywords");
					
			}
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
		}else if (ctt==VSS2Lexer.STRING_LITERAL||ctt==VSS2Lexer.IDENTIFIER){
			return ct.getText().replace("'", "").replace("\"", "");
		}else if (ctt==VSS2Lexer.INTEGER_LITERAL){
			return Integer.valueOf(ct.getText());
		}else if (ctt==VSS2Lexer.FLOAT_LITERAL){
			return Double.valueOf(ct.getText());
		}else if (ctt==VSS2Lexer.DOT){
			return internalOps.dot;
		}else{
			System.out.println("Nothing to return!"+ctx.toStringTree(parser));
			return null;
		}
			
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

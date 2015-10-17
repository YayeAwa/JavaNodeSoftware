package org.vamdc.tapservice.vss2.impl4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.Map;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.BaseRestrictExpression;
import org.vamdc.tapservice.vsssqlparser.VSS2Lexer;

public class RestrictExpression4 extends BaseRestrictExpression{
	
	public RestrictExpression4(ParseTree ctx,Collection<Restrictable> filter) {
		CommonToken ct = (CommonToken)ctx.getPayload();
		this.operator=ExprMap.get(ct.getType());
		
		ParseTree parent = ctx.getParent();
		
		for (int i=0;i<parent.getChildCount();i++){
			ParseTree child = parent.getChild(i);
			if (child!=ctx){
				TerminalNodeImpl tn = findKeyword(child);
				if (tn.getPayload() instanceof CommonToken){
					CommonToken ctl = (CommonToken)tn.getPayload();
					if (ctl.getType()==VSS2Lexer.IDENTIFIER){
						String columnName=ctl.getText();
						Restrictable keyword=null;
						try{
							 keyword = Restrictable.valueOfIgnoreCase(columnName);
						}catch (IllegalArgumentException e){
							throw new IllegalArgumentException("Keyword "+columnName+" is not present in the dictionary.");
						}
						if (filter==null || filter.contains(keyword)){
							this.keyword = keyword;
							if (i>0)
								this.operator=inverseOperator(this.operator);
						}else{
							throw new IllegalArgumentException("Keyword "+keyword+" is valid but is restricted by filter.");
						}
					}else if (ctl.getType()==VSS2Lexer.STRING_LITERAL){
						this.values.add(ctl.getText().replace("'", "").replace("\"", ""));
					}else if (ctl.getType()==VSS2Lexer.INTEGER_LITERAL){
						this.values.add(Integer.valueOf(ctl.getText()));
					}else if (ctl.getType()==VSS2Lexer.FLOAT_LITERAL){
						this.values.add(Double.valueOf(ctl.getText()));
					}			
				}
			}
		}
		System.out.println("RE4"+this);
		//System.out.println("RE4"+this.keyword+"("+this.operator+")"+ctx.getClass()+parent.getClass());
		
	}

	public RestrictExpression4(ArrayList<Object> children) {
		for (Object child:children){
			if (child instanceof Operator){
				this.operator=(Operator) child;
			}
		}
		for (int i=0;i<children.size();i++){
			Object child = children.get(i); 
			if (child instanceof Restrictable){
				this.keyword=(Restrictable) child;
				if (i>0)
					this.operator=inverseOperator(this.operator);
			}else if (child instanceof String || child instanceof Double || child instanceof Integer){
				this.values.add(child);
			}	
		}
		System.out.println(this);
	}

	private TerminalNodeImpl findKeyword(ParseTree tree){
		if (tree.getChildCount()>1)
			throw  new IllegalArgumentException("Too many child nodes in the tree "+tree.toStringTree());
		
		if(!(tree instanceof TerminalNodeImpl) && tree.getChildCount()>0)
			return findKeyword(tree.getChild(0));
		else if (tree instanceof TerminalNodeImpl)
			return (TerminalNodeImpl)tree;
		return null;
	}
	
	public static boolean supportsOperation(Integer key){
		return ExprMap.containsKey(key);
	}
	
	/**
	 * Map between string operator definition and Operator id's
	 * here is all we currently support for restricts comparison.
	 */
	 static Map<Integer, Operator> ExprMap = new TreeMap<Integer,Operator>(){
		private static final long serialVersionUID = -1239582483152716476L;

	{
		put(VSS2Lexer.EQ,Operator.EQUAL_TO);
		put(VSS2Lexer.ASSIGN,Operator.EQUAL_TO);
		put(VSS2Lexer.NOT_EQ1,Operator.NOT_EQUAL_TO);
		put(VSS2Lexer.NOT_EQ2,Operator.NOT_EQUAL_TO);
		put(VSS2Lexer.LT,Operator.LESS_THAN);
		//put(VSS2Lexer.LT2,Operator.LESS_THAN);
		put(VSS2Lexer.GT,Operator.GREATER_THAN);
		//put(VSS2Lexer.GT2,Operator.GREATER_THAN);
		put(VSS2Lexer.LT_EQ,Operator.LESS_THAN_EQUAL_TO);
		put(VSS2Lexer.GT_EQ,Operator.GREATER_THAN_EQUAL_TO);
		put(VSS2Lexer.K_BETWEEN,Operator.BETWEEN);
		put(VSS2Lexer.K_IN,Operator.IN);
		put(VSS2Lexer.K_LIKE,Operator.LIKE);
	}};
	
	/**
	 * For operators, that can be inversed to accomodate incoming query column and value swapping, return inverted
	 * For all other, return initial operator
	 * @param initial Operator to inverse, if possible
	 * @return inverted operator
	 */
	private Operator inverseOperator(Operator initial){
		switch(initial){
		case GREATER_THAN:
			return Operator.LESS_THAN;
		case LESS_THAN:
			return Operator.GREATER_THAN;
		case GREATER_THAN_EQUAL_TO:
			return Operator.LESS_THAN_EQUAL_TO;
		case LESS_THAN_EQUAL_TO:
			return Operator.GREATER_THAN_EQUAL_TO;
		default:
			return initial;
		}
	}
}

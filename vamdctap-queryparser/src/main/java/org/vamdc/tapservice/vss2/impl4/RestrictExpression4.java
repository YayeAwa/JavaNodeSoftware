package org.vamdc.tapservice.vss2.impl4;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.BaseRestrictExpression;
import org.vamdc.tapservice.vsssqlparser.VSS2Lexer;

public class RestrictExpression4 extends BaseRestrictExpression{

	public RestrictExpression4(ArrayList<Object> children,boolean mode){
		//Special mode to fill in only the prefix and restrictable
		for (Object child:children){
			if (child instanceof String){
				this.prefixStr=(String) child;
			}else if (child instanceof Restrictable){
				this.keyword=(Restrictable)child;
			}
		}
	}
	
	public RestrictExpression4(ArrayList<Object> children) {
		for (Object child:children){
			if (child instanceof Operator){
				this.operator=(Operator) child;
			}
		}
		for (int i=0;i<children.size();i++){
			Object child = children.get(i); 
			if (child instanceof String || child instanceof Double || child instanceof Integer){
				this.values.add(child);
			}
			if (child instanceof Restrictable){
				this.keyword=(Restrictable) child;
				if (i>0)
					this.operator=inverseOperator(this.operator);
			}
			if (child instanceof RestrictExpression4){
				//We got a prefixed Restrictable as a child, in fact.
				this.keyword=((RestrictExpression4) child).getColumn();
				this.prefixStr=((RestrictExpression4) child).getPrefixStr();
				if (i>0)
					this.operator=inverseOperator(this.operator);
			}
		}
		System.out.println(this);
	}

	
	public static boolean supportsOperation(Integer key){
		return ExprMap.containsKey(key);
	}
	
	public 
	
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

	public static boolean supportsOperatior(Operator child) {
		return ExprMap.containsValue(child);
	}
}

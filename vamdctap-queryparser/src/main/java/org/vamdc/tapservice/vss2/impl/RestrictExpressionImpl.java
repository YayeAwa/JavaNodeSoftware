package org.vamdc.tapservice.vss2.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.Prefix;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.tapservice.vsssqlparser.CommonTreeDumper;

/**
 * Implementation of vss2 RestrictExpression interface,
 * self-building from the relevant ANTLR subtree 
 * @author doronin
 *
 */
@SuppressWarnings("serial")
public class RestrictExpressionImpl implements RestrictExpression {

	private Prefix columnPrefix;
	private String prefixStr;
	private Restrictable keyword;
	private Operator operator;
	private Collection<Object> values;
	
	@Override
	public Restrictable getColumn() {
		return keyword;
	}

	@Override
	public String getColumnName() {
		return keyword.name();
	}

	@Override
	public Operator getOperator() {
		return operator;
	}

	@Override
	public Prefix getPrefix() {
		return columnPrefix;
	}

	@Override
	public Object getValue() {
		if (values!=null && values.size()>0)
			return values.iterator().next();
		return null;
	}

	@Override
	public Collection<Object> getValues() {
		return values;
	}

	@Override
	public boolean isValid() {
		return keyword!=null && operator!=null && !values.isEmpty();
	}
	
	/**
	 * Default constructor accepts and parses subtree with expression information.
	 * @param expressionBranch subtree with expression information
	 * @param columnIDMap Column IDs to map from string column names
	 */
	public RestrictExpressionImpl(CommonTree expressionBranch, Collection<Restrictable> filter) throws IllegalArgumentException{
		String columnName = CommonTreeTools.findColumnName(expressionBranch).toUpperCase();
		
		if (columnName==null || columnName.length()==0)
			throw new IllegalArgumentException("restrict expression was not valid"+CommonTreeDumper.toString(expressionBranch));
		
		Restrictable keyword=null;
		try{
			 keyword = Restrictable.valueOfIgnoreCase(columnName);
		}catch (IllegalArgumentException e){
			throw new IllegalArgumentException("Keyword "+columnName+" is not present in the dictionary.");
		}
				
		if (filter==null || filter.contains(keyword)){
			this.keyword = keyword;
		}else{
			throw new IllegalArgumentException("Keyword "+keyword+" is valid but is restricted by filter.");
		}
		
		prefixStr = CommonTreeTools.findColumnPrefix(expressionBranch);
		
		String operatorStr = expressionBranch.getText().toUpperCase();
		operator = ExprMap.get(operatorStr);
		if (CommonTreeTools.checkInverse(expressionBranch))
			operator = inverseOperator(operator);

		values = new ArrayList<Object>();
		for (CommonTree value:CommonTreeTools.findObjects(expressionBranch)){
			values.add(getObjectValue(value));
		}

	}
	
	/**
	 * Get prefix string
	 * @return prefix string as it was in the query
	 */
	public String getPrefixStr(){
		return prefixStr;
	}
	
	/**
	 * Set prefix structure
	 * @param prefix structure with the keyword from the dictionary and an index.
	 */
	public void setPrefix(Prefix prefix){
		this.columnPrefix = prefix;
	}
	
	/**
	 * Map between string operator definition and Operator id's
	 * here is all we currently support for restricts comparison.
	 */
	private static Map<String,Operator> ExprMap = new HashMap<String,Operator>(){{
		put("=",Operator.EQUAL_TO);
		//put("eq",Operators.EQUAL_TO);
		put("<>",Operator.NOT_EQUAL_TO);
		put("!=",Operator.NOT_EQUAL_TO);
		put("<",Operator.LESS_THAN);
		put(">",Operator.GREATER_THAN);
		put("<=",Operator.LESS_THAN_EQUAL_TO);
		put(">=",Operator.GREATER_THAN_EQUAL_TO);
		put("BETWEEN",Operator.BETWEEN);
		put("IN_VALUES",Operator.IN);
		put("LIKE",Operator.LIKE);
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
		}
		return initial;
	}
	
	/**
	 * Get value from subtree
	 * @param mytree subtree containing only one object
	 * @return
	 */
	private Object getObjectValue(CommonTree mytree){
		if (mytree.getText().equals("STRING_LITERAL")){
			return mytree.getChild(0).getText().replace("'", "").replace("\"", "");
		}else if (mytree.getText().equals("INTEGER_LITERAL")){
			return Integer.parseInt(mytree.getChild(0).getText());
		}else if (mytree.getText().equals("FLOAT_LITERAL")){
			return Double.parseDouble(mytree.getChild(0).getText());
		}
		return null;
	}
	
	@Override
	public String toString(){
		String result="";
		if (getPrefix()!=null)
			result=getPrefix()+".";
		result+=getColumnName();
		
		result=result.concat(" "+getOperator().toString()+" ");
		
		if (getValues().size()>1)
			result=result.concat("(");
		Iterator<Object> iter = getValues().iterator();
		while (iter.hasNext()){
			Object restrict=iter.next();
			if (restrict instanceof String)
				result=result.concat("'"+restrict+"'");
			else
				result=result.concat(restrict.toString());
			if (iter.hasNext())
				result=result.concat(",");
		}
		if (getValues().size()>1)
			result=result.concat(")");

		return result;
	}
	
}

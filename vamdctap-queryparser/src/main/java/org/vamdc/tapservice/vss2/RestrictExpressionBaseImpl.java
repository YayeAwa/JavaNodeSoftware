package org.vamdc.tapservice.vss2;

import java.util.ArrayList;
import java.util.Collection;

import org.vamdc.dictionary.Restrictable;

public class RestrictExpressionBaseImpl implements RestrictExpression{

	protected Prefix columnPrefix;
	protected String prefixStr;
	protected Restrictable keyword;
	protected Operator operator;
	protected Collection<Object> values=new ArrayList<Object>();
	
	public RestrictExpressionBaseImpl() {
		super();
	}

	@Override
	public Restrictable getColumn() {
		return keyword;
	}

	@Override
	public String getColumnName() {
		if (this.keyword!=null)
			return keyword.name();
		else 
			return "";
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
	 * Get prefix string
	 * @return prefix string as it was in the query
	 */
	public String getPrefixStr() {
		return prefixStr;
	}

	/**
	 * Set prefix structure
	 * @param prefix structure with the keyword from the dictionary and an index.
	 */
	public void setPrefix(Prefix prefix) {
		this.columnPrefix = prefix;
	}

	@Override
	public String toString() {
		StringBuilder result=new StringBuilder();
		if (getPrefix()!=null)
			result.append(getPrefix()).append(".");
		else if (getPrefixStr()!=null)
			result.append(getPrefixStr()).append(":");
			
		result.append(getColumnName());
		
		if (this.getOperator()!=null)
			result.append(" ").append(getOperator().toString()).append(" ");
		
		if (getValues().size()>1)
			result.append("(");
		
		for (Object restrict:getValues()){
			if (restrict instanceof String)
				result.append("'").append(restrict).append("'");
			else
				result.append(restrict.toString());
			result.append(",");
		}
		if (getValues().size()>0)
			result.deleteCharAt(result.length()-1);//Delete the last comma of a series
		if (getValues().size()>1)
			result.append(")");
	
		return result.toString();
	}

}
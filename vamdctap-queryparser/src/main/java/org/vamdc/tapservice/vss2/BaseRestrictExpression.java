package org.vamdc.tapservice.vss2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.vamdc.dictionary.Restrictable;

public class BaseRestrictExpression implements RestrictExpression{

	protected Prefix columnPrefix;
	protected String prefixStr;
	protected Restrictable keyword;
	protected Operator operator;
	protected Collection<Object> values=new ArrayList<Object>();
	
	public BaseRestrictExpression() {
		super();
	}

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
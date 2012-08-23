package org.vamdc.tapservice.vss2;

import java.util.Collection;

import org.vamdc.dictionary.Restrictable;

/**
 * Expression corresponding to a single restriction keyword
 * 
 */
public interface RestrictExpression extends LogicNode{
	

	
	/**
	 * Get column prefix
	 * @return Prefix, having keyword and index
	 */
	public Prefix getPrefix();
	
	/**
	 * Get restriction keyword
	 * @return
	 */
	public Restrictable getColumn();
	
	/**
	 * Get a string representation of a restriction keyword
	 * @return
	 */
	public String getColumnName();
	
	/**
	 * Get an operator applied to the keyword
	 * @return Operator
	 */
	public Operator getOperator();
	
	
	/**
	 * Get a list of values a column is compared to. Can be one ore more of a String, Double or Integer
	 * @return
	 */
	public Collection<Object> getValues();

	/**
	 * Get a single value for relevant comparisons
	 * @return
	 */
	public Object getValue();
	
	/**
	 * Check if expression is valid and has all required attributes
	 * @return
	 */
	public boolean isValid();
	
	/**
	 * Print out the expression in a string form
	 * @return
	 */
	public String toString();
	
}

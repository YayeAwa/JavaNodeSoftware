package org.vamdc.tapservice.vss2;

import java.util.Collection;

/**
 * Expression corresponding to a single logic node of a tree
 * 
 */
public interface LogicNode {
	
	/**
	 * Available logic and comparison operators
	 * @author doronin
	 *
	 */
	public static enum Operator{
		NLO,
		AND,
		OR,
		NOT,
		EQUAL_TO,
		NOT_EQUAL_TO,
		LESS_THAN,
		GREATER_THAN,
		LESS_THAN_EQUAL_TO,
		GREATER_THAN_EQUAL_TO,
		BETWEEN,
		IN,
		LIKE,
		LIKE_IGNORE_CASE,
		ADD,
		SUBTRACT,
		MULTIPLY,
		DIVIDE,
		NEGATIVE,
		TRUE,
		FALSE,
		OBJ_PATH,
		DB_PATH,
		LIST,
		NOT_BETWEEN,
		NOT_IN,
		NOT_LIKE,
		NOT_LIKE_IGNORE_CASE,
	};
	
	/**
	 * Get an operator applied to child nodes
	 * @return Operator
	 */
	public Operator getOperator();
	
	
	/**
	 * Get a list of child nodes
	 * @return child LogicNode or RestrictExpression
	 */
	public Collection<?> getValues();

	/**
	 * Get a first child node (useful when the child node is the only one, i.e. with NOT operator)
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

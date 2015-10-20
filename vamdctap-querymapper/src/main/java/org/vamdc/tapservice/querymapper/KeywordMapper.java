package org.vamdc.tapservice.querymapper;

import org.apache.cayenne.exp.Expression;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.RestrictExpression;

/**
 * Interface describing a Restrictable keyword mapper from VSS2 queries
 * to internal database queries with apache cayenne.
 * Mapping relative to multiple root tables is supported, each root table 
 * is distinguished by a corresponding queryIndex
 *
 */
public interface KeywordMapper {
	
	/**
	 * Translate RestrictExpression into cayenne Expression, intended for the query #queryIndex 
	 * @param input RestrictExpression to translate
	 * @param queryIndex index of an expression
	 * @return Apache Cayenne Expression that can be added to the translated logic tree
	 */
	public Expression translate(RestrictExpression input,int queryIndex);

	/**
	 * The same as Translate, but with possibility to alter path components for Cayenne path aliases.
	 * Aliasing enables for example to translate query with "AND" 
	 * for linked species table in case of different prefixes
	 * @param input
	 * @param queryIndex
	 * @param alias
	 * @param aliasReplace
	 * @return
	 */
	public Expression translateAlias(RestrictExpression input, int queryIndex,
			String alias, String aliasReplace);

	
	/**
	 * Get a keyword that this mapper operates on
	 * @return Restrictable to be mapped
	 */
	public Restrictable getKeyword();
	
	/**
	 * Get maximum query index that can be applied to translate().
	 * This method is used for verification purposes, all mappers must give the same value
	 * @return maximum query index 
	 */
	public int getMaxQueryIndex();

	
}

package org.vamdc.tapservice.querymapper;

import java.util.Collection;

import org.apache.cayenne.exp.Expression;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.LogicNode;

/**
 * Interface describing the advanced QueryMapper.
 * It provides support for keyword-specific mappers (KeywordMapper interface),
 * enabling unit conversions, virtual keywords, etc.
 */
public interface QueryMapper {
	
	/**
	 * Add new mapper to use
	 * @param mapper KeywordMapper implementation
	 */
	public QueryMapper addKeywordMapper(KeywordMapper mapper);
	
	/**
	 * Get supported Restrictables
	 * @return collection of supported Restrictable keywords
	 */
	public Collection<Restrictable> getRestrictables();
	
	
	/**
	 * Map a tree
	 * @param root root element of logic tree
	 * @param queryIndex index of a query to report to KeywordMappers
	 * @return mapped Expression
	 */
	public Expression mapTree(LogicNode root, int queryIndex);
	
	/**
	 * Same as MapTree, but use aliases in query path
	 * @param root
	 * @param queryIndex
	 * @param alias
	 * @param replacement
	 * @return
	 */
	public Expression mapAliasedTree(LogicNode root, int queryIndex, String alias,
			String replacement);
	
	/**
	 * Do internal integrity check
	 * @return true if mapper is operational
	 */
	public boolean isReady();


	
}

package org.vamdc.tapservice.vss2;

import java.util.Collection;
import java.util.List;

import org.vamdc.dictionary.Requestable;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.dictionary.VSSPrefix;


/**
 * VSS2 library interface
 * 
 * @author Misha Doronin
 */
public interface Query {

	/**
	 * Check if a certain branch is selected.
	 * @param branch branch name from Requestables dictionary
	 * @return true if branch is directly specified or is inherited from dependent branch, like molecules are inherited from states request.
	 */
	public boolean checkSelectBranch(Requestable branch);
	
	/**
	 * Get a list of prefixes present in a query
	 * @return
	 */
	public Collection<Prefix> getPrefixes();
	
	/**
	 * Get restrictables in a form of a list
	 * @return Collection<RestrictExpression>
	 */
	public List<RestrictExpression> getRestrictsList();
	
	/**
	 * Get restrictables in a proper tree-form
	 *  @return root LogicNode
	 */
	public LogicNode getRestrictsTree();
	
	/**
	 * Get filtered logic tree, containing only specified keywords.
	 * @param allowedKeywords collection of allowed keywords
	 * @return filtered tree
	 */
	public LogicNode getFilteredTree(Collection<Restrictable> allowedKeywords);
	
	/**
	 * Get filtered logic tree containing only keywords with the specified prefix and it's index.
	 * If prefix is null, resulting tree would contain only unprefixed keywords.
	 * @param prefix
	 * @param index
	 * @return
	 */
	public LogicNode getPrefixedTree(VSSPrefix prefix, int index);
	
	/**
	 * Get the original query string.
	 * Can be used for logging or something similar.
	 * @return Original query string.
	 */
	public String getQuery();
	
	/**
	 * toString method for debugging purposes
	 * @return string representation of a query
	 */
	public String toString();
}

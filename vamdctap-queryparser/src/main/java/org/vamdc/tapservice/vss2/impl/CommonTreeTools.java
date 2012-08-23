package org.vamdc.tapservice.vss2.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;

/**
 * Collection of static methods to do tricks with ANTLR CommonTree's
 * @author doronin
 *
 */
public class CommonTreeTools {

	/**
	 * Find all occurrences of any of tokens, return all matching subtrees in a list.
	 * Recursively calls itself.
	 * @param baseTree tree where to look for tokens
	 * @param searchTokens array of String tokens to look for
	 * @return foundBranches list of CommonTree's.
	 */
	public static Collection<CommonTree> findAllMatches(CommonTree baseTree, String[] searchTokens){
		Collection<CommonTree> branches = new ArrayList<CommonTree>();
		if (baseTree!=null && baseTree.getText()!=null){
			boolean notAdded = true;
			for (String thisMatch: searchTokens){
				if (baseTree.getText().equalsIgnoreCase(thisMatch)){
					branches.add(baseTree);
					notAdded=false;
					break;
				}
			}
			if (notAdded)
				for (int i = 0; i < baseTree.getChildCount(); i++) {			
					branches.addAll(findAllMatches((CommonTree)baseTree.getChild(i),searchTokens));
				}		
		}
		return branches;
	}
	
	/**
	 * Find first occurrence of any of tokens, return subtree.
	 * Recursively calls itself.
	 * @param baseTree tree where to look for tokens
	 * @param searchTokens array of String tokens to look for
	 * @return first matched CommonTree branch.
	 */
	public static CommonTree findFirstMatch(CommonTree baseTree, String[] searchTokens){
		if (baseTree!=null && baseTree.getText()!=null){	
			for (String thisMatch: searchTokens)
				if (baseTree.getText().equalsIgnoreCase(thisMatch))
					return baseTree;
			for (int i = 0; i < baseTree.getChildCount(); i++) {			
				CommonTree found= findFirstMatch((CommonTree)baseTree.getChild(i),searchTokens);
				if (found!=null) return found;
			}
		}
		return null;
	}
	
	/**
	 * Find the WHERE branch
	 * @param tree query tree
	 * @return where subtree
	 */
	public static CommonTree findWhere(CommonTree tree){
		String[] words = {"WHERE"};
		return findFirstMatch(tree,words);
	}
	
	/**
	 * Find select columns root element
	 * @param tree query tree
	 * @return columns (requestables) subtree
	 */
	public static CommonTree findSelectColumns(CommonTree tree){
		String[] words = {"COLUMNS"};
		return findFirstMatch(tree,words);
	}
	
	/**
	 * Find column name in subtree.
	 * <p>Is used in RestrictExpression constructor
	 * @param tree query subtree
	 * @return column name
	 */
	public static String findColumnName(CommonTree tree){
		String[] words = {"COLUMN_EXPRESSION"};
		CommonTree column = findFirstMatch(tree,words);
		if (column!=null && column.getChildCount()>0)
			return column.getChild(0).getText();
		return "";
	}
	
	/**
	 * Get a column prefix of a current column.
	 * <p>Is used in RestrictExpression constructor
	 * @param tree expression subtree
	 * @return column prefix name
	 */
	public static String findColumnPrefix(CommonTree tree){
		String[] words = {"COLUMN_EXPRESSION"};
		CommonTree expr = findFirstMatch(tree,words);
		//Check if we have a child and it has also a child, return grandchild's text as a prefix name 
		if (expr!=null && expr.getChildCount()>0){
			expr=(CommonTree) expr.getChild(0);
			if (expr.getChildCount()>0){
				expr=(CommonTree) expr.getChild(0);
				return expr.getText();
			}
		}
		//Usually there is no column prefix at all, return an empty string
		return "";
	}
	
	/**
	 * Check if someone have put the comparison value in front of a column expression in query.
	 * Used by RestrictExpression to check if we need to inverse comparison operation.
	 * @param tree query subtree
	 * @return true if literals come before expression
	 */
	public static boolean checkInverse(CommonTree tree){
		String[] words = {"COLUMN_EXPRESSION","STRING_LITERAL","INTEGER_LITERAL","FLOAT_LITERAL"};
		CommonTree column = findFirstMatch(tree,words);
		
		return column!=null && !column.getText().equals("COLUMN_EXPRESSION");
	}
	
	/**
	 * Fill list of variables in expression from tree.
	 * <p>Is used in RestrictExpression constructor
	 * @param mytree
	 * @return list of subtrees containing variables
	 */
	public static Collection<CommonTree> findObjects(CommonTree mytree){
		String[] words = {"STRING_LITERAL","INTEGER_LITERAL","FLOAT_LITERAL"};
		return findAllMatches(mytree,words);
	}
	
}

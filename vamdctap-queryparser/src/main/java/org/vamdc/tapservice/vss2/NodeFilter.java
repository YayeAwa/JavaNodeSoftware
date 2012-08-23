package org.vamdc.tapservice.vss2;

import java.util.Collection;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.LogicNode.Operator;
import org.vamdc.tapservice.vss2.impl.LogicNodeImpl;

/**
 * Collection of static methods for LogicNode filtering
 * @author doronin
 *
 */
public class NodeFilter {

	/**
	 * Filters existing logic tree to leave only nodes having specific prefix
	 * @param node - root of the original logic tree
	 * @param filter - Prefix, that should be present.
	 * @return filtered logic tree
	 */
	public static LogicNode filterPrefix(LogicNode node, Prefix filter) {
		if (node == null || filter == null)  return null;
		if (node instanceof RestrictExpression){//Check expression if we are on expression node
			Prefix pref = ((RestrictExpression)node).getPrefix();
			if (pref!=null && pref.equals(filter))
				return node;
			else if(pref==null && filter.getPrefix()==null)//Support getting only unprefixed keywords
				return node;
			return null;
		}else if (node.getValues()!=null && node.getValues().size()>0){
			LogicNodeImpl newNode = new LogicNodeImpl(node.getOperator());//Create new node
			for (Object child:node.getValues()){//Check all child nodes
				LogicNode filtered=filterPrefix((LogicNode)child,filter);
				if (filtered!=null)//Add child nodes if they passed filtering
					newNode.addChild(filtered);
			}
			if (newNode.getValues().size()>1||(newNode.getValues().size()==1 && newNode.getOperator().equals(Operator.NOT) ))//Check if we have enough child nodes
				return newNode;
			else if (newNode.getValues().size()==1)//If not - return first child
				return (LogicNode)newNode.getValue();
			else
				return null;//If we ended up without child nodes, return null.
		}else 
			return null;//If we don't have child nodes, return null.
	}
	
	

	/**
	 * Filters existing logic tree to leave only nodes having specific prefix
	 * @param node - root of the original logic tree
	 * @param filter - Collection<Restrictable>, that should be present.
	 * @return filtered logic tree
	 */
	public static LogicNode filterKeywords(LogicNode node, Collection<Restrictable> filter) {
		if (node == null || filter == null) return null;
		if (node instanceof RestrictExpression){//Check expression if we are on expression node
			if (checkAllowedKeywords((RestrictExpression) node,filter))
				return node; 
			return null;
		}else if (node.getValues()!=null && node.getValues().size()>0){
			LogicNodeImpl newNode = new LogicNodeImpl(node.getOperator());//Create new node
			for (Object child:node.getValues()){//Check all child nodes
				LogicNode filtered=filterKeywords((LogicNode)child,filter);
				if (filtered!=null)//Add child nodes if they passed filtering
					newNode.addChild(filtered);
			}
			if (newNode.getValues().size()>1||(newNode.getValues().size()==1 && newNode.getOperator().equals(Operator.NOT) ))//Check if we have enough child nodes
				return newNode;
			else if (newNode.getValues().size()==1)//If not - return first child
				return (LogicNode)newNode.getValue();
			else
				return null;//If we ended up without child nodes, return null.
		}else 
			return null;//If we don't have child nodes, return null.
	}
	
	private static boolean checkAllowedKeywords(RestrictExpression expr,Collection<Restrictable> filter) {
		return ((filter!=null && expr!=null && filter.contains(expr.getColumn()))
				|| (filter==null && expr!=null));
	}
	
}

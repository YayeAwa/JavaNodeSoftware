package org.vamdc.tapservice.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.RestrictExpression;

/**
 * Simple query mapper from VSSParser tree to Cayenne Expression structures.
 * Only one-to-one mapping is supported, without unit conversions or other processing.
 * For more advanced query mapping library see org.vamdc.tapservice.querymapper package.
 * 
 */
@Deprecated
public class QueryMapper {
	/**
	 * Simple mapper from the VSS LogicNode tree to Cayenne Expression
	 * @param tree VSSParser LogicNode tree
	 * @param pathSpec Map<Integer,String> map from "restrictables" identifiers to cayenne paths
	 * @return Expression 
	 */
	public static Expression mapTree(LogicNode tree,Map<Restrictable,String> pathSpec){
		if (tree==null)
			return null;
		if (tree instanceof RestrictExpression){
			//We are at the end of the branch, build Expression from RestrictExpression
			RestrictExpression expr = (RestrictExpression)tree;
			return buildExpression(expr,pathSpec.get(expr.getColumn()));
		}else{
			Expression result=null;//Result expression
			Expression newExpr = null;//Expression for a child node
			switch (tree.getOperator()){
				case AND:
					//AND all child node's Expressions
					for (Object branch:tree.getValues()){
						newExpr = mapTree((LogicNode)branch,pathSpec);
						if (result==null)
							result = newExpr;
						else
							result = result.andExp(newExpr);
					}
					return result;
				case OR:
					//OR all child node's Expressions
					for (Object branch:tree.getValues()){
						newExpr = mapTree((LogicNode)branch,pathSpec);
						if (result==null)
							result = newExpr;
						else if (newExpr!=ExpressionFactory.expTrue())
							result = result.orExp(newExpr);
					}
					return result;
				case NOT:
					//Inverse the child tree
					return mapTree((LogicNode) tree.getValue(),pathSpec).notExp();
				default:
					//Return true expression just in case
					return ExpressionFactory.expTrue();
			}
		}
	}
	
	public static Expression listAnd(Collection<RestrictExpression> restrictsList,Map<Restrictable,String> pathSpec){
		//Add all restrictables
		
		Expression result = null;
		Expression expr;
		for (RestrictExpression thisrestr:restrictsList){
			expr = buildExpression(thisrestr,pathSpec.get(thisrestr.getColumn()));
			if (result==null) 
				result = expr;
			else
				result = result.andExp(expr);
		}
		return result;
	}
	/**
	 * Build Cayenne Expression from VSS2 RestrictExpression
	 * @param restrictor VSS2 RestrictExpression
	 * @param pathSpec path specification in Cayenne model relations tree
	 * @return Cayenne Expression corresponding to the RestrictExpression and it's path
	 */
	private static Expression buildExpression(RestrictExpression restrictor,String pathSpec){
		//If we didn't specify restrictor, or path is undefined, treat it as boolean true
		if (restrictor.getOperator()==null || pathSpec==null){
			return ExpressionFactory.expTrue();
		}else{
			Object value = restrictor.getValue();	//For most cases we will need this value
			//Check all known operators, try to handle them
			switch (restrictor.getOperator()){
			case EQUAL_TO:
				return ExpressionFactory.matchExp(pathSpec, value);
			case NOT_EQUAL_TO:
				return ExpressionFactory.noMatchExp(pathSpec, value);
			case LESS_THAN:
				return ExpressionFactory.lessExp(pathSpec, value);
			case GREATER_THAN:
				return ExpressionFactory.greaterExp(pathSpec, value);
			case LESS_THAN_EQUAL_TO:
				return ExpressionFactory.lessOrEqualExp(pathSpec, value);
			case GREATER_THAN_EQUAL_TO:
				return ExpressionFactory.greaterOrEqualExp(pathSpec, value);
			case BETWEEN:
				if (restrictor.getValues().size()==2){
					Iterator<Object> iter = restrictor.getValues().iterator();
					return ExpressionFactory.betweenExp(pathSpec, iter.next(), iter.next());
				}
					
			case IN:
				return ExpressionFactory.inExp(pathSpec, restrictor.getValues());
			case LIKE:
				return ExpressionFactory.likeExp(pathSpec, value);
			}
		}
		return ExpressionFactory.expFalse();
	}
	
	
}


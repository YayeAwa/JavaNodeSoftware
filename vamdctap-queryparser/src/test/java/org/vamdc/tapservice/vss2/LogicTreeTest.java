package org.vamdc.tapservice.vss2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.LogicNode.Operator;

public class LogicTreeTest{
	
	@Test
	public void testTree(){
		String query = "Select * where IonCharge=0 AND InChIKey like 'String%'";
		Query qp = VSSParser.parse(query);
		assertNotNull( qp.getRestrictsTree() );
		assertEquals( qp.getRestrictsTree().getOperator(),Operator.AND);
		for (Object node:qp.getRestrictsTree().getValues()){
			if (node instanceof LogicNode){
				LogicNode child = (LogicNode) node;
				assertTrue (child.getOperator()==Operator.EQUAL_TO || child.getOperator()==Operator.LIKE);
				assertTrue (child instanceof RestrictExpression);
				assertEquals(child.getValues().size(),1);
			}else{
				fail();
			}
		}
		assertEquals(((RestrictExpression)qp.getRestrictsTree().getValues().toArray()[0]).getColumn(),Restrictable.IonCharge);
		assertEquals(((RestrictExpression)qp.getRestrictsTree().getValues().toArray()[1]).getColumn(),Restrictable.InchiKey);
	}

	
	/**
	 * Test query with three AND's, they should result in one logicNode with three children
	 */
	@Test
	public void testThreeAnd(){
		String query = "Select * where IonCharge=0 AND AtomSymbol like 'String%' AND AtomSymbol like 'String2%'";
		Query qp = VSSParser.parse(query);
		assertEquals(qp.getRestrictsTree().getValues().size(),3);
		for (Object child:qp.getRestrictsTree().getValues()){
			if (child instanceof LogicNode){
				LogicNode node = (LogicNode) child;
				assertTrue(node.getOperator()==Operator.EQUAL_TO || node.getOperator() == Operator.LIKE);
			}
		}
	}
	
	/**
	 * Test query with three OR's, they should result in one logicNode with three children
	 */
	@Test
	public void testThreeOR(){
		String query = "Select * where IonCharge=0 OR IonCharge=1 OR IonCharge=1;";
		Query qp = VSSParser.parse(query);
		assertTrue(qp.getRestrictsTree().getValues().size()==3);
		for (Object child:qp.getRestrictsTree().getValues()){
			assertTrue(child instanceof LogicNode);
			assertTrue(((LogicNode) child).getOperator()==Operator.EQUAL_TO);
		}
	}
	
	/**
	 * Test query tree with Not
	 */
	@Test
	public void testOrAndNot(){
		String query = "Select * where (IonCharge=0 AND NOT (IonCharge=1 AND IonCharge='col4')) OR IonCharge=1;";
		Query qp = VSSParser.parse(query);
		Object[] children;
		
		assertEquals(qp.getRestrictsTree().getValues().size(),2);
		assertEquals(qp.getRestrictsTree().getOperator(),Operator.OR);
		
		children = qp.getRestrictsTree().getValues().toArray();
		assertEquals(((LogicNode)children[0]).getOperator(),Operator.AND);
		assertEquals(((LogicNode)children[1]).getOperator(),Operator.EQUAL_TO);
		
		children = ((LogicNode)children[0]).getValues().toArray();
		assertEquals(((LogicNode)children[0]).getOperator(),Operator.EQUAL_TO);
		assertEquals(((LogicNode)children[0]).getValue(),0);
		assertEquals(((LogicNode)children[1]).getOperator(),Operator.NOT);
		
		assertEquals(((LogicNode)
				((LogicNode)children[1]).getValue()).getOperator(),Operator.AND);
	}
	
	

	
	

}

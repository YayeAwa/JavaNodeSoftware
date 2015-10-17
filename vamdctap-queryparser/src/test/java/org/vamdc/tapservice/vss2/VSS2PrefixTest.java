package org.vamdc.tapservice.vss2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.dictionary.VSSPrefix;
import org.vamdc.tapservice.vss2.LogicNode.Operator;


public class VSS2PrefixTest{

	@Test
	public void testNullPrefix(){
		String query = "Select * where InchiKey=2 OR atomMass=1;";
		Query qp = VSSParser.parse(query);
		
		RestrictExpression firstChild = (RestrictExpression)qp.getRestrictsTree().getValue();
		Prefix pref = firstChild.getPrefix();
		assertNull(pref);
	}
	
	/**
	 * Test column prefixes in a simple query
	 */
	@Test
	public void testWherePrefix(){
		String query = "Select * where reactant1.InchiKey=2 OR reactant1.atomMass=1;";
		Query qp = VSSParser.parse(query);
		
		assertTrue(qp.getRestrictsTree().getValues().size()==2);
		assertEquals(qp.getRestrictsTree().getOperator(),Operator.OR);
		RestrictExpression firstChild = (RestrictExpression)qp.getRestrictsTree().getValue();
		assertEquals(firstChild.getColumn(),Restrictable.InchiKey);
		Prefix pref = firstChild.getPrefix();
		assertNotNull(pref);
		assertEquals(pref.getPrefix(),VSSPrefix.REACTANT);
		assertEquals(pref.getIndex(),0);
	}
	
	/**
	 * Test double occurrence of the same prefix and it's display in the prefix list
	 */
	@Test
	public void testDoublePrefix(){
		String query = "Select * where (reactant1.InchiKey=2 OR reactant1.atomMass=1) AND reactant2.InchiKey=0;";
		Query qp = VSSParser.parseDebug(query);

		assertEquals(qp.getPrefixes().size(),2);
		assertEquals(qp.getPrefixes().iterator().next().getPrefix(),VSSPrefix.REACTANT);
	}
	
	/**
	 * Test single product and single reactant
	 */
	public void testProductReactant(){
		String query = "select * where reactant1.MoleculeChemicalName=\"CRP\"  and product1.MoleculeChemicalName=\"CH\";";
		Query qp = VSSParser.parse(query);
		assertEquals(2,qp.getPrefixes().size());
		for (Prefix pref:qp.getPrefixes()){
			assertEquals(0,pref.getIndex());
		}
			
	}
	
	

}

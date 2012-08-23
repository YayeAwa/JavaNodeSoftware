package org.vamdc.tapservice.vss2;


import static org.junit.Assert.*;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.dictionary.VSSPrefix;
import org.vamdc.tapservice.vss2.LogicNode.Operator;

public class TreeFilterTest{

	private Collection<Restrictable> filter;
	private String query;
	
	@Before
	public void init(){
		query=null;
		filter=null;
	}
	
    /**
     * Test if we can pass null to the columns filter
     */
	@Test
    public void testRestrictsNoFilter(){
    	
		query = "select * where InchiKey='blablabla' and IonCharge=1 and MoleculeMolecularWeight>10";
    	
    	Query qp = VSSParser.parse(query, null);
    	assertTrue(qp.getRestrictsList().size()==3);
    	assertEquals(qp.getRestrictsList().get(0).getColumn(),Restrictable.InchiKey);
    	assertEquals(qp.getRestrictsList().get(1).getColumn(),Restrictable.IonCharge);
    	assertEquals(qp.getRestrictsList().get(2).getColumn(),Restrictable.MoleculeMolecularWeight);
    }
	
	/**
	 * Test filtered query with Not
	 */
	@Test
	public void testFilterNot(){
		query = "Select * where (ioncharge=0 AND NOT (inchikey=1 AND atomsymbol='col4')) OR ioncharge=1;";
		
		Query qp = VSSParser.parse(query);
		
		filter = EnumSet.of(Restrictable.InchiKey);
		
		LogicNode tree = qp.getFilteredTree(filter);
		assertTrue(tree.getValues().size()==1);
		assertEquals(tree.getOperator(),Operator.NOT);
		
		assertTrue(tree.getValue() instanceof RestrictExpression);
	}
	

	
	/**
	 * Test the column filter, and test if expressions are floating up to the root of the tree if they are left alone
	 */
	@Test
	public void testColumnFilter(){
		query = "Select * where (ioncharge>1 OR inchikey=0) AND (ioncharge<1 OR atomSymbol=1);";
		
		Query qp = VSSParser.parse(query);
		
		filter = EnumSet.of(Restrictable.InchiKey,Restrictable.AtomSymbol);
		
		for (Object child : qp.getFilteredTree(filter).getValues() ){
			if (child instanceof LogicNode){
				LogicNode childNode = (LogicNode) child;
				assertEquals (childNode.getOperator(),Operator.EQUAL_TO);
			}else{
				fail();
			}
		}
	}
		
	/**
	 * Test parsing and grouping by prefixes
	 */
	@Test
	public void testPrefixFilter(){
		query = "Select * where " +
				"((Reactant1.atomMass=2 OR reactant1.atomMass=1) AND Reactant2.MoleculeStoichiometricFormula='CO') " +
				"OR (Reactant1.atomMass=4 AND Reactant2.MoleculeStoichiometricFormula='CS');";
		Query qp = VSSParser.parse(query);
		
		System.out.println(qp.getPrefixedTree(VSSPrefix.REACTANT, 0).toString());
		System.out.println(qp.getPrefixedTree(VSSPrefix.REACTANT, 1).toString());
		
		assertEquals(qp.getPrefixes().size(),2);
		assertNotNull(qp.getPrefixedTree(VSSPrefix.REACTANT, 1));
		assertNull(qp.getPrefixedTree(VSSPrefix.REACTANT, 2));
	}
	
	/**
	 * Test filtering unprefixed query by prefix
	 */
	@Test
	public void testPrefixFilterUnprefixed(){
		query = "Select * where (inchikey=0 AND NOT (inchikey=1 AND inchikey='col4')) OR inchikey=1;";
		Query qp = VSSParser.parse(query);
		assertNull(qp.getPrefixedTree(VSSPrefix.REACTANT, 0));
		assertNotNull(qp.getRestrictsTree());
	}
	
	/**
	 * Test filtering mixed prefixed-unprefixed query by prefix
	 */
	@Test
	public void testPrefixFilterMixed(){
		query = "Select * where (reactant1.inchikey=0 AND NOT (atomsymbol=1 AND inchikey='col4')) OR inchikey=1;";
		Query qp = VSSParser.parse(query);
		
		assertNotNull(qp.getPrefixedTree(VSSPrefix.REACTANT, 0));
		assertNotNull(qp.getRestrictsTree());
		assertEquals(qp.getPrefixes().size(),1);
		assertNull(qp.getPrefixedTree(VSSPrefix.REACTANT, 2));
	}
	
	/**
	 * Test filtering mixed prefixed-unprefixed query by null prefix
	 */
	@Test
	public void testPrefixFilterMixedNull(){
		String query = "Select * where (reactant1.inchikey=0 AND NOT (inchiKey=1 AND inchikey='col4')) OR inchikey=1;";
		Query qp = VSSParser.parse(query);
		assertNotNull(qp.getPrefixedTree(null, 0));
		assertNotNull(qp.getRestrictsTree());
		assertNotSame(qp.getRestrictsTree(),qp.getPrefixedTree(null,0));
		assertNull(qp.getPrefixedTree(VSSPrefix.REACTANT, 2));
	}
	
}

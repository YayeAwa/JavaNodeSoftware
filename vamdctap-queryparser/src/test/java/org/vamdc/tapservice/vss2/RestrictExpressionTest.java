package org.vamdc.tapservice.vss2;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.Test;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.LogicNode.Operator;

public class RestrictExpressionTest{
	
    /**
     * Test if variable's data types are recognized correctly
     */
	@Test
    public void testRestrictsDataType(){
    	String query = "select * where InchiKey = 'stringval' AND atomMass=10 AND ioncharge=10.5";
    	Collection<Restrictable> filter = EnumSet.of(Restrictable.InchiKey, Restrictable.AtomMass,Restrictable.IonCharge);
    	
    	Query qp = VSSParser.parseDebug(query, filter);
    	assertTrue(qp.getQuery().equals(query));
    	assertFalse(qp.getQuery().equals("notaquery"));
    	assertEquals(qp.getRestrictsList().size(),3);
    	assertTrue(qp.getRestrictsList().get(0).getValue() instanceof String);
    	assertTrue(qp.getRestrictsList().get(1).getValue() instanceof Integer);
    	assertTrue(qp.getRestrictsList().get(2).getValue() instanceof Double);
    }
    
    /**
     * Test if restricts are transitive to column name and value swapping
     */
	@Test
    public void testRestrictsSwap(){
    	String query = "select * where InchiKey>=10 AND 10<=InchiKey AND 10>InchiKey";
    	Query qp = VSSParser.parseDebug(query);
    	assertTrue(qp.getRestrictsList().get(0).getOperator().equals(Operator.GREATER_THAN_EQUAL_TO));
    	assertTrue(qp.getRestrictsList().get(1).getOperator().equals(Operator.GREATER_THAN_EQUAL_TO));
    	assertTrue(qp.getRestrictsList().get(2).getOperator().equals(Operator.LESS_THAN));
    }
	
}

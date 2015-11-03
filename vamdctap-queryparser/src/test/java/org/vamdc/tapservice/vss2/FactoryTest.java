package org.vamdc.tapservice.vss2;

import static org.junit.Assert.*;

import java.util.EnumSet;

import org.junit.Ignore;
import org.junit.Test;
import org.vamdc.dictionary.Restrictable;

/**
 * Tests for the VSSParser static factory
 *
 */
public class FactoryTest {
	
	@Test
	public void testParseQuery(){
		String query = "Select * where atomSymbol='Fe'";
		Query parsed = VSSParser.parse(query);
		assertTrue(parsed.getRestrictsTree()!=null);
		assertEquals(parsed.getRestrictsTree().getValue(),"Fe");
	}
	
	@Test
	public void testParseQueryWithFilterException(){
		String query = "Select * where atomSymbol='Fe' and inchikey='random'";
		
		Query parsed = VSSParser.parse(query,EnumSet.of(Restrictable.AtomSymbol,Restrictable.InchiKey));
		assertTrue(parsed.getRestrictsTree()!=null);
		assertTrue(parsed.getRestrictsList().size()==2);
	}
	
	@Test
	public void testParseAnotherQuery(){
		String query="SELECT ALL WHERE ( AtomSymbol = 'Mg' ) AND (RadTransWavelength >= 5100 AND RadTransWavelength <= 5200);";
		Query parsed = VSSParser.parse(query);
		assertTrue(parsed.getRestrictsTree()!=null);
	}
	
	@Test
	@Ignore
    public void speedTest(){
    	long startTime = System.currentTimeMillis();
    	long cumul=0;
    	Integer myval=0;
    	String query="";
    	for (int i=0;i<1000;i++){
    		query="select * where inchikey="+i+" and (atomsymbol='Ar' or ioncharge=100 or atomsymbol='Xe');";
    		Query parsed=VSSParser.parse(query);
    		
    		myval=(Integer)((RestrictExpression)parsed.getRestrictsTree().getValue()).getValue();
    		cumul+=myval;
    		if (i!=myval)
    			System.out.println("i!=myval at"+i);
    	}
    	long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println( "Hello World! I have iterated "+(myval+1)+" times and summed up "+cumul+" taking "+elapsedTime+"ms "+"for queries like "+query);
    }
	
}

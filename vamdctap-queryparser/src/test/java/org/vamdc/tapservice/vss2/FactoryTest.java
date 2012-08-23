package org.vamdc.tapservice.vss2;

import static org.junit.Assert.*;

import java.util.EnumSet;

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
	
}

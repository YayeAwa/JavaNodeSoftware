package org.vamdc.tapservice.vss2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.vamdc.dictionary.Requestable;

public class RequestableTest {


    /**
     * Test the checkSelectBranch
     */
	@Test
    public void testCheckSelectBranchStar()
    {
    	String query = "Select * where AtomSymbol='Fe'";
    	Query qp = VSSParser.parse(query);
        for (Requestable req: Requestable.values())
        	assertTrue( qp.checkSelectBranch(req));
    }
	
	/**
     * Test if we can select something unknown
     */
	@Test(expected=IllegalArgumentException.class)
    public void testSelectUnknown(){
    	String query = "Select unknown";
    	Query qp = VSSParser.parse(query);
    	
    	assertTrue(qp.checkSelectBranch(Requestable.Atoms));
    }
	
	
	 /**
     * Test the checkSelectBranch
     */
	@Test
    public void testCheckSelectBranchAll()
    {
    	String query = "Select All where AtomSymbol='Fe'";
    	Query qp = VSSParser.parse(query);
        for (Requestable req: Requestable.values())
        	assertTrue( qp.checkSelectBranch(req));
    }
    
    /**
     * Test if we can select returned blocks of data
     */
	@Test
    public void testSelectColumns(){
    	String query = "Select molecules,atoms";
    	Query qp = VSSParser.parse(query);
    	
    	assertTrue(qp.checkSelectBranch(Requestable.Atoms));
    	assertFalse(qp.checkSelectBranch(Requestable.States));
    	assertTrue(qp.checkSelectBranch(Requestable.Molecules));
    	assertFalse(qp.checkSelectBranch(Requestable.Collisions));
    }
    
    /**
     * Test if checkColumn tests are following logc
     */
	@Test
    public void testSelectRecursiveColumns(){
    	String query = "Select states";
    	Query qp = VSSParser.parse(query);
    	assertTrue(qp.checkSelectBranch(Requestable.Atoms));
    	assertTrue(qp.checkSelectBranch(Requestable.States));
    	assertTrue(qp.checkSelectBranch(Requestable.Molecules));
    	assertFalse(qp.checkSelectBranch(Requestable.Collisions));
    }
    
    /**
     * Test if Select Species works for all species
     */
	@Test
    public void testSelectSpecies(){
    	String query = "Select species";
    	Query qp = VSSParser.parse(query);
    	assertTrue(qp.checkSelectBranch(Requestable.Atoms));
    	assertFalse(qp.checkSelectBranch(Requestable.States));
    	assertTrue(qp.checkSelectBranch(Requestable.Molecules));
    	assertFalse(qp.checkSelectBranch(Requestable.Collisions));
    }
	
    /**
     * Test if Select Species works for all species
     */
	@Test
    public void testSelectSpeciesSC(){
    	String query = "Select species;";
    	Query qp = VSSParser.parse(query);
    	assertTrue(qp.checkSelectBranch(Requestable.Atoms));
    	assertFalse(qp.checkSelectBranch(Requestable.States));
    	assertTrue(qp.checkSelectBranch(Requestable.Molecules));
    	assertFalse(qp.checkSelectBranch(Requestable.Collisions));
    }
    
    /**
     * Test if we can keep query inside QueryParser
     */	
	@Test
    public void testStoredQuery(){
    	String query = "select * where Inchikey = 'stringval' AND atomMass=10 AND ioncharge=10.5";
    	Query qp = VSSParser.parse(query);
    	assertTrue(qp.getQuery().equals(query));
    	assertFalse(qp.getQuery().equals("notaquery"));
	}
    
}

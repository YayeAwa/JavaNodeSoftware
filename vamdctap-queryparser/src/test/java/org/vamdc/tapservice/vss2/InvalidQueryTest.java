package org.vamdc.tapservice.vss2;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.EnumSet;

import org.junit.Test;
import org.vamdc.dictionary.Restrictable;

public class InvalidQueryTest{

	@Test(expected=IllegalArgumentException.class)
	public void testMixedQuotes(){
		String query = "select * where InchiKey = 'stringval\"";
		Query qp = VSSParser.parse(query);
		
		assertTrue(qp.getRestrictsTree().isValid());
	}
	
	/**
	 * Test to reveal a null-pointer exception on invalid query
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNullColumn(){
		String query="select * where (inchikey = 'keyword' qwerty or inchikey='blah')";

		parseAndFail(query);
	}

	/**
	 * Test to reveal IllegalArgumentException on unsupported keyword in the query.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testParseQueryWithFilterException(){
		String query = "Select * where atomSymbol='Fe' and inchikey='random'";

		VSSParser.parse(query,EnumSet.of(Restrictable.AtomSymbol));

		fail();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testParseTypoElectQuery(){
		String query = "elect * where atomSymbol='Fe'";

		VSSParser.parse(query);
		fail();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testParseTypoWehreQuery(){
		String query = "select * wehre atomSymbol='Fe'";

		VSSParser.parse(query, null);
		parseAndFail(query);
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testNonSenseQuery(){
		String query = "select * where";
		
		parseAndFail(query);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSelectWrongRequestable(){
		String query = "select something where";
		parseAndFail(query);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseQueryWithMissingBrace(){
		String query="select * where (inchikey = 'keyword' or inchikey='blah'";

		parseAndFail(query);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testParseQueryWithExtraBrace(){
		String query="select * where (inchikey = 'keyword' or inchikey='blah'))";
		
		parseAndFail(query);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseExtraAnd(){
		String query="select * where (RadTransWavelength >= 5000000.0 AND RadTransWavelength <= 6000000.0) AND ((MoleculeChemicalName = 'Ethene' AND MoleculeStoichiometricFormula = 'C2H4' AND ))";
		parseAndFail(query);
	}
	
	
	private void parseAndFail(String query) {
		VSSParser.parse(query);

		fail();
	}
}

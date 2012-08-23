package org.vamdc.tapservice.vss2;


import static org.junit.Assert.*;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.vamdc.dictionary.Restrictable;

public class InvalidQueryTest{

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	/**
	 * Setup expected exception
	 */
	public void expectException(){
		exception.expect(IllegalArgumentException.class);
	}


	/**
	 * Test to reveal a null-pointer exception on invalid query
	 */
	@Test
	public void testNullColumn(){
		String query="select * where (inchikey = 'keyword' qwerty or inchikey='blah')";

		parseAndFail(query);
	}

	/**
	 * Test to reveal IllegalArgumentException on unsupported keyword in the query.
	 */
	@Test
	public void testParseQueryWithFilterException(){
		String query = "Select * where atomSymbol='Fe' and inchikey='random'";

		VSSParser.parse(query,EnumSet.of(Restrictable.AtomSymbol));

		fail();
	}

	@Test
	public void testParseTypoElectQuery(){
		String query = "elect * where atomSymbol='Fe'";

		parseAndFail(query);
	}

	@Test
	public void testParseTypoWehreQuery(){
		String query = "select * wehre atomSymbol='Fe'";

		VSSParser.parseDebug(query, null);
		parseAndFail(query);
	}

	
	@Test
	public void testNonSenseQuery(){
		String query = "select * where";
		
		parseAndFail(query);
	}
	
	@Test
	public void testSelectWrongRequestable(){
		String query = "select something where";
		parseAndFail(query);
	}
	
	@Test
	public void testParseQueryWithMissingBrace(){
		String query="select * where (inchikey = 'keyword' or inchikey='blah'";

		parseAndFail(query);
	}

	@Test
	@Ignore
	public void testParseQueryWithExtraBrace(){
		String query="select * where (inchikey = 'keyword' or inchikey='blah'))";
		
		parseAndFail(query);
	}
	
	@Test
	public void testParseExtraAnd(){
		String query="select * where (RadTransWavelength >= 5000000.0 AND RadTransWavelength <= 6000000.0) AND ((MoleculeChemicalName = 'Ethene' AND MoleculeStoichiometricFormula = 'C2H4' AND ))";
		parseAndFail(query);
	}
	
	
	private void parseAndFail(String query) {
		VSSParser.parse(query);

		fail();
	}
}

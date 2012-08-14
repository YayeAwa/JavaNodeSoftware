package org.vamdc.tapservice.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SimpleTest extends TestCase
{


	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( SimpleTest.class );
	}

	/**
	 * Test if we can test
	 */
	public void testApp()
	{
		assertTrue( true );
	}
}

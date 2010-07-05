package org.json;

import junit.framework.TestCase;

public class JSONTester extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	public void testJSONArray() throws Exception {
		
		JSONArray j = new JSONArray( "[dsadsdas,dsdsdsdsd]" );
		
		String s = (String)j.get( 0 );
		assertEquals(  "dsadsdas", s);
		
		
	}

	
	
}

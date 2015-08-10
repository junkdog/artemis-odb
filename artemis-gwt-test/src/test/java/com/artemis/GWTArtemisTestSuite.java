package com.artemis;

import com.artemis.io.ReferenceTrackerTest;
import com.artemis.io.ComponentLookupSerializerTest;
import junit.framework.TestCase;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.junit.tools.GWTTestSuite;

public class GWTArtemisTestSuite extends TestCase {
	@SuppressWarnings("unchecked")
	private static Class<? extends GWTTestCase>[] tests = new Class[] {
		WorldTest.class,
		FactoryWireTest.class,
		EntityFactoryTest.class,
		ExtendedEntityFactoryTest.class,

		// reflection
		ReferenceTrackerTest.class
		//ComponentLookupSerializerTest.class - does not work at this time.
	};

	public static GWTTestSuite suite() {
		GWTTestSuite suite = new GWTTestSuite("artemis-odb gwt tests");
		for (Class<? extends TestCase> tc : tests) {
			suite.addTestSuite(tc);
		}
		return suite;
	}
}

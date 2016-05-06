package com.artemis;

import com.artemis.io.InputStreamHelperTest;
import com.artemis.io.ReferenceTrackerTest;
import com.artemis.link.EntityLinkManagerTest;
import com.artemis.link.FieldMutatorTest;
import com.artemis.link.LinkFactoryTest;
import com.artemis.managers.CustomJsonWorldSerializationManagerTest;
import com.artemis.managers.JsonWorldSerializationManagerTest;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.TestCase;

public class GWTArtemisTestSuite extends TestCase {
	@SuppressWarnings("unchecked")
	private static Class<? extends GWTTestCase>[] tests = new Class[]{
			WorldTest.class,
			FactoryWireTest.class,
			EntityFactoryTest.class,
			ExtendedEntityFactoryTest.class,

			// reflection
			ReferenceTrackerTest.class,
			CustomJsonWorldSerializationManagerTest.class,
			JsonWorldSerializationManagerTest.class,
			InputStreamHelperTest.class,

			// links
			FieldMutatorTest.class,
			LinkFactoryTest.class,
			EntityLinkManagerTest.class
	};

	public static GWTTestSuite suite() {
		GWTTestSuite suite = new GWTTestSuite("artemis-odb gwt tests");
		for (Class<? extends TestCase> tc : tests) {
			suite.addTestSuite(tc);
		}
		return suite;
	}
}

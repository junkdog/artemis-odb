/**
 * 
 */
package com.artemis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.artemis.WorldConfigurationBuilder.Priority;
import com.artemis.utils.Bag;
import com.artemis.utils.Sort;

/**
 * @author Eric
 *
 */
public class ConfigurationElementTest {
	
	private Bag<ConfigurationElement<Object>> elementBag;
	
	private ConfigurationElement<Object> obj1;
	private ConfigurationElement<Object> obj2;
	private ConfigurationElement<Object> obj3;
	private ConfigurationElement<Object> obj4;
	private ConfigurationElement<Object> obj5;
	private ConfigurationElement<Object> obj6;
	
	
	@Before
	public void setup() {
		elementBag = new Bag<ConfigurationElement<Object>>();
		
		obj1 = new ConfigurationElement<Object>(new Object(), Priority.HIGHEST);	// Index = 0
		obj2 = new ConfigurationElement<Object>(new Object(), Priority.LOWEST);		// Index = 4
		obj3 = new ConfigurationElement<Object>(new Object(), Priority.NORMAL);		// Index = 3
		obj4 = new ConfigurationElement<Object>(new Object(), Priority.LOWEST);		// Index = 5
		obj5 = new ConfigurationElement<Object>(new Object(), Priority.HIGHEST);	// Index = 1
		obj6 = new ConfigurationElement<Object>(new Object(), Priority.HIGHEST);	// Index = 2
		
		elementBag.add(obj1);
		elementBag.add(obj2);
		elementBag.add(obj3);
		elementBag.add(obj4);
		elementBag.add(obj5);
		elementBag.add(obj6);
		
	}
	
	@Test
	public void test_sorting() {
		Sort.instance().sort(elementBag);
		
		assertTrue(elementBag.get(0) == obj1);
		assertTrue(elementBag.get(1) == obj5);
		assertTrue(elementBag.get(2) == obj6);
		assertTrue(elementBag.get(3) == obj3);
		assertTrue(elementBag.get(4) == obj2);
		assertTrue(elementBag.get(5) == obj4);
	}

}

package com.artemis.component;

import com.artemis.PooledComponent;
import com.artemis.utils.Bag;

public class ReusedComponent extends PooledComponent
{
	public String data;
//	public Bag<String> strings = new Bag<String>();
//	{
//		data = "reused";
//		strings.add("s1");
//		strings.add(null);
//		strings.add("s3");
//	}
	@Override
	public void reset()
	{
		
	}
}

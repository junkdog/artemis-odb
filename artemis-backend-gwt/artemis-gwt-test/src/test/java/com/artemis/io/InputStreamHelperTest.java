package com.artemis.io;

import com.google.gwt.junit.client.GWTTestCase;
import junit.framework.Assert;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@SuppressWarnings("static-method")
public class InputStreamHelperTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	public void test_isMarkSupported_accepts_bytearrayInputStream() {
		InputStreamHelper.isMarkSupported(newValidStream());
	}

	public void test_isMarkSupported_refuses_other_stream() throws Exception {
		try {
			InputStreamHelper.isMarkSupported(newIllegalStream());
			Assert.fail();
		} catch(RuntimeException ignore){
		}
	}

	public void test_reset_accepts_bytearrayInputStream() {
		try {
			InputStreamHelper.reset(newValidStream());
		} catch (IOException ignore) {
		}
	}

	public void test_reset_refuses_other_stream() throws Exception {
		try {
			InputStreamHelper.reset(newIllegalStream());
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	private ByteArrayInputStream newValidStream() {
		return new ByteArrayInputStream(new byte[10]);
	}

	private DataInputStream newIllegalStream() {
		return new DataInputStream(newValidStream());
	}
}

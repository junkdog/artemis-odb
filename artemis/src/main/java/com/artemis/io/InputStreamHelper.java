package com.artemis.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream bridge helper.
 *
 * @author Daan van Yperen
 */
public class InputStreamHelper {
	private InputStreamHelper() {}

	/** Reset input stream */
	public static void reset(InputStream is) throws IOException {
		is.reset();
	}

	/**
	 * Tests if this input stream supports the <code>mark</code> and
	 * <code>reset</code> methods. Whether or not <code>mark</code> and
	 * <code>reset</code> are supported is an invariant property of a
	 * particular input stream instance. The <code>markSupported</code> method
	 * of <code>InputStream</code> returns <code>false</code>.
	 *
	 * @return  <code>true</code> if this stream instance supports the mark
	 *          and reset methods; <code>false</code> otherwise.
	 * @see     InputStream#mark(int)
	 * @see     InputStream#reset()
	 */
	public static boolean isMarkSupported(InputStream is)
	{
		return is.markSupported();
	}
}

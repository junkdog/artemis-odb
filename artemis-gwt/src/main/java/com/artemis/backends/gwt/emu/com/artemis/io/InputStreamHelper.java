package com.artemis.io;

import com.artemis.utils.reflect.ClassReflection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream bridge helper.
 *
 * @author Daan van Yperen
 */
public class InputStreamHelper {
	private InputStreamHelper() {
	}

	/**
	 * Resets the buffer to the marked position.  The marked position
	 * is 0 unless another position was marked or an offset was specified
	 * in the constructor.
	 */
	public static void reset(InputStream is) throws IOException {
		ensureIsByteArrayInputStream(is);
		((ByteArrayInputStream) is).reset();
	}

	/**
	 * Tests if this input stream supports the <code>mark</code> and
	 * <code>reset</code> methods. Whether or not <code>mark</code> and
	 * <code>reset</code> are supported is an invariant property of a
	 * particular input stream instance. The <code>markSupported</code> method
	 * of <code>InputStream</code> returns <code>false</code>.
	 *
	 * @return <code>true</code> if this stream instance supports the mark
	 * and reset methods; <code>false</code> otherwise.
	 * @see java.io.InputStream#mark(int)
	 * @see java.io.InputStream#reset()
	 */
	public static boolean isMarkSupported(InputStream is) {
		ensureIsByteArrayInputStream(is);
		return ((ByteArrayInputStream) is).markSupported();
	}

	private static void ensureIsByteArrayInputStream(InputStream is) {
		if (!isByteArrayInputStream(is))
			throw new RuntimeException(is.getClass() + "not supported. world-io only supports ByteArrayInputStream.");
	}

	private static boolean isByteArrayInputStream(InputStream is) {
		return ByteArrayInputStream.class.equals(is.getClass());
	}
}

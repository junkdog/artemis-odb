package java.lang;

public class SecurityException extends RuntimeException {
	public SecurityException() {
		super();
	}

	public SecurityException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecurityException(String s) {
		super(s);
	}

	public SecurityException(Throwable cause) {
		super(cause);
	}
}

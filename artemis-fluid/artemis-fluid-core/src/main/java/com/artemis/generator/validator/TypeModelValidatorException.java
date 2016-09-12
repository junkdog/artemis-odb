package com.artemis.generator.validator;

/**
 * @author Daan van Yperen
 */
public class TypeModelValidatorException extends RuntimeException {
    public TypeModelValidatorException() {
    }

    public TypeModelValidatorException(String message) {
        super(message);
    }

    public TypeModelValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeModelValidatorException(Throwable cause) {
        super(cause);
    }
}

package org.acme.openapiclient.exception;

/**
 * Custom exception for Pet Store operations.
 */
public class PetStoreException extends RuntimeException {

    public PetStoreException(String message) {
        super(message);
    }

    public PetStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}


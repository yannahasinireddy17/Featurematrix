package com.productcompare.exception;

public class DuplicateStoreException extends RuntimeException {
    public DuplicateStoreException(String message) {
        super(message);
    }
}

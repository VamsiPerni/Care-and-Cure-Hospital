package com.cac.exception;


@SuppressWarnings("serial")
public class BillNotFound extends RuntimeException {

    public BillNotFound(String message) {
        super(message);
    }
}

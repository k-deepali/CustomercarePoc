package com.agsft.customer.Care.exception;

import org.springframework.http.HttpStatus;

public class CustomerException extends RuntimeException{


    private int code;
    private String message;
    public CustomerException(String message) {
        super(message);
    }

    public CustomerException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    public CustomerException(HttpStatus code, String message) {
        super(message);
        this.code = code.value();
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.agsft.customer.Care.exception;

public class ServiceException extends RuntimeException {

	int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public ServiceException(int code, String message) {

		super(message);
		this.code = code;
	}

	public ServiceException(String message) {
		super(message);
	}

}

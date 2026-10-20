package com.mycom.springjdbcclientdemo1.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatusCode;
	private final String errorCode;
	private final boolean printStackTrace;

	public CustomException(ErrorCode errorCode, String message) {
		this(errorCode, message, null);
	}

	public CustomException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.httpStatusCode = errorCode.getHttpStatus();
		this.errorCode = errorCode.getCode();
		this.printStackTrace = true;
	}
}

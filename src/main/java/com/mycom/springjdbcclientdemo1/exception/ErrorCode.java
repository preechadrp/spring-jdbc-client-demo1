package com.mycom.springjdbcclientdemo1.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// ---- external API ----
	EXTERNAL_API_REJECTED(HttpStatus.BAD_GATEWAY, "EXT-001"),
	EXTERNAL_API_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "EXT-002"),

	// ---- ทั่วไป (COM) ----
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COM-001"),
	REQUEST_REJECTED(HttpStatus.BAD_REQUEST, "COM-002"),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COM-999"),

	// ---- order (ORD) ----
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD-001"),

	// ---- ตัวอย่างใน HelloWorldController (DEMO) ----
	NAME_NOT_ALLOWED(HttpStatus.NOT_FOUND, "DEMO-001");

	private final HttpStatus httpStatus;
	private final String code;

	ErrorCode(HttpStatus httpStatus, String code) {
		this.httpStatus = httpStatus;
		this.code = code;
	}

}

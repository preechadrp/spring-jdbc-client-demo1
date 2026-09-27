package com.mycom.springjdbcclientdemo1.exception;

import lombok.Getter;

/**
 * Exception สำหรับเก็บรายละเอียด error ที่ได้รับจาก API ภายนอก
 *
 * errorCode และ message ที่ส่งให้ client มาจาก ErrorCode ของระบบเรา
 * ส่วน externalErrorCode และ externalErrorMessage ใช้เก็บรายละเอียดจากระบบปลายทาง
 * โดยปกติจะใช้สำหรับ log เป็นหลัก แต่ business error ที่อนุญาตสามารถตั้ง
 * exposeExternalError=true เพื่อส่ง code/message ของ external ต่อให้ client ได้
 */
@Getter
public class ExternalApiException extends CustomException {

	private static final long serialVersionUID = 1L;

	private final String provider;
	private final int upstreamStatus;
	private final String externalErrorCode;
	private final String externalErrorMessage;
	private final boolean exposeExternalError;

	public ExternalApiException(
			ErrorCode errorCode,
			String provider,
			int upstreamStatus,
			String externalErrorCode,
			String externalErrorMessage,
			Throwable cause) {
		this(errorCode, provider, upstreamStatus, externalErrorCode,
				externalErrorMessage, false, cause);
	}

	public ExternalApiException(
			ErrorCode errorCode,
			String provider,
			int upstreamStatus,
			String externalErrorCode,
			String externalErrorMessage,
			boolean exposeExternalError,
			Throwable cause) {
		super(errorCode, errorCode.getDefaultMessage(), cause);
		this.provider = provider;
		this.upstreamStatus = upstreamStatus;
		this.externalErrorCode = externalErrorCode;
		this.externalErrorMessage = externalErrorMessage;
		this.exposeExternalError = exposeExternalError;
	}
}

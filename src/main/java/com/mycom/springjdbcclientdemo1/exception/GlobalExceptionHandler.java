package com.mycom.springjdbcclientdemo1.exception;

import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * รูปแบบ JSON ที่ตอบกลับเมื่อเกิด error
	 * เช่น {"errorCode":"ORD-001","errorMessage":"ไม่พบ order id 99"}
	 * (HTTP status อยู่ใน status line ของ response แล้ว จึงไม่ใส่ซ้ำใน body)
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private record ErrorResponseDto(String errorCode, String errorMessage, String externalErrorCode) {

		private ErrorResponseDto(String errorCode, String errorMessage) {
			this(errorCode, errorMessage, null);
		}
	};

	// ดักจับ error จาก API ภายนอกโดยเฉพาะ ต้องประกาศก่อน CustomException
	@ExceptionHandler(ExternalApiException.class)
	public ResponseEntity<ErrorResponseDto> handleExternalApiException(ExternalApiException ex) {
		if (ex.isExposeExternalError()) {
			// ใช้เฉพาะ business error ที่ผ่านการอนุญาตให้ส่งต่อแล้ว
			log.warn("[external-business-error] provider={} upstreamStatus={} externalCode={} externalMessage={}",
					ex.getProvider(), ex.getUpstreamStatus(),
					ex.getExternalErrorCode(), ex.getExternalErrorMessage());

			return ResponseEntity.status(ex.getUpstreamStatus())
					.body(new ErrorResponseDto(
							ex.getExternalErrorCode(),
							ex.getExternalErrorMessage()));
		}

		log.error("[{}] provider={} upstreamStatus={} externalCode={} externalMessage={}",
				ex.getErrorCode(), ex.getProvider(), ex.getUpstreamStatus(),
				ex.getExternalErrorCode(), ex.getExternalErrorMessage(), ex);

		return ResponseEntity.status(ex.getHttpStatusCode())
				.body(new ErrorResponseDto(
						ex.getErrorCode(),
						ex.getMessage(),
						ex.getExternalErrorCode()));
	}

	// ดักจับ CustomException ที่เราสร้างเอง
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException ex) {

		HttpStatus status = ex.getHttpStatusCode();

		// 4xx = ผู้ใช้ส่งข้อมูลผิด (warn), 5xx = ระบบมีปัญหา (error)
		if (status.is5xxServerError()) {
			if (ex.isPrintStackTrace()) {
				log.error("[{}] {}", ex.getErrorCode(), ex.getMessage(), ex);
			} else {
				log.error("[{}] {}", ex.getErrorCode(), ex.getMessage());
			}
		} else {
			log.warn("[{}] {}", ex.getErrorCode(), ex.getMessage());
		}

		// 5xx: ไม่ส่งข้อความภายในให้ client (อาจมีรายละเอียดของระบบ) ดูรายละเอียดจาก log แทน
		String message = status.is5xxServerError()
				? ErrorCode.INTERNAL_ERROR.getDefaultMessage()
				: ex.getMessage();

		return ResponseEntity.status(status).body(new ErrorResponseDto(ex.getErrorCode(), message));
	}

	// ดักจับ @Valid ที่ไม่ผ่าน (เดิมตกไปที่ Exception ทั่วไปและได้ 500)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
				.collect(Collectors.joining(", "));
		log.warn("[{}] {}", ErrorCode.VALIDATION_FAILED.getCode(), message);

		return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getHttpStatus())
				.body(new ErrorResponseDto(ErrorCode.VALIDATION_FAILED.getCode(), message));
	}

	// ดักจับ JSON ที่ผิดรูปแบบ เช่น วันที่ผิด format หรือ JSON ไม่ครบ (เดิมได้ 500)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDto> handleUnreadable(HttpMessageNotReadableException ex) {
		log.warn("[{}] {}", ErrorCode.REQUEST_REJECTED.getCode(), ex.getMessage());
		return ResponseEntity.status(ErrorCode.REQUEST_REJECTED.getHttpStatus()).body(new ErrorResponseDto(
				ErrorCode.REQUEST_REJECTED.getCode(), "รูปแบบข้อมูล JSON ไม่ถูกต้อง"));
	}

	// ดักจับ Exception ทั่วไปที่ไม่ได้คาดคิด (เช่น NullPointerException)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {

		// exception มาตรฐานของ Spring (เช่น 404 ไม่พบ path, 405 method ผิด, JSON ผิดรูปแบบ)
		// มี status ของตัวเองอยู่แล้ว ใช้ status นั้นแทนการตอบ 500
		if (ex instanceof ErrorResponse errorResponse) {
			HttpStatusCode status = errorResponse.getStatusCode();
			log.warn("[{}] {} {}", ErrorCode.REQUEST_REJECTED.getCode(), status.value(), ex.getMessage());
			return ResponseEntity.status(status).body(new ErrorResponseDto(
					ErrorCode.REQUEST_REJECTED.getCode(), ErrorCode.REQUEST_REJECTED.getDefaultMessage()));
		}

		log.error("[{}] {}", ErrorCode.INTERNAL_ERROR.getCode(), ex.getMessage(), ex);
		// ไม่ต่อ ex.getMessage() ท้ายข้อความเหมือนเดิม เพราะอาจเผย SQL/ชื่อตาราง/โครงสร้างระบบ
		return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getHttpStatus()).body(new ErrorResponseDto(
				ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getDefaultMessage()));
	}
}

package com.mycom.springjdbcapidemo1.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * exception สำหรับข้อผิดพลาดทางธุรกิจ แปลงเป็น JSON โดย GlobalExceptionHandler
 *
 *   httpStatusCode : HTTP status ที่ตอบกลับ (เดิมใช้ int errorCode ทำหน้าที่นี้)
 *   errorCode      : รหัสธุรกิจแบบ String เช่น "ORD-001"
 *
 * ทั้งสองค่ามาจาก ErrorCode enum เสมอ จึงไม่มีทางได้คู่ที่ไม่ตรงกัน
 * หรือ status ที่ไม่มีอยู่จริง (เดิม HttpStatus.valueOf(999) จะพังตอนตอบกลับ)
 *
 * ตัวอย่าง
 *   throw new CustomException(ErrorCode.ORDER_NOT_FOUND);                      // ใช้ข้อความเริ่มต้น
 *   throw new CustomException(ErrorCode.ORDER_NOT_FOUND, "ไม่พบ order id 99");  // ข้อความเฉพาะกรณี
 *   throw new CustomException(ErrorCode.INTERNAL_ERROR, "เรียก API ภายนอกไม่ได้", e); // เก็บสาเหตุเดิมไว้ใน log
 */
@Getter
public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatusCode;
	private final String errorCode;
	private final boolean printStackTrace;

	public CustomException(ErrorCode errorCode) {
		this(errorCode, errorCode.getDefaultMessage(), false, null);
	}

	public CustomException(ErrorCode errorCode, String message) {
		this(errorCode, message, false, null);
	}

	public CustomException(ErrorCode errorCode, String message, boolean printStackTrace) {
		this(errorCode, message, printStackTrace, null);
	}

	/** ห่อ exception เดิมไว้ (cause) — stack trace ของต้นเหตุจริงจะถูกพิมพ์ใน log ด้วย */
	public CustomException(ErrorCode errorCode, String message, Throwable cause) {
		this(errorCode, message, true, cause);
	}

	private CustomException(ErrorCode errorCode, String message, boolean printStackTrace, Throwable cause) {
		super(message, cause);
		this.httpStatusCode = errorCode.getHttpStatus();
		this.errorCode = errorCode.getCode();
		this.printStackTrace = printStackTrace;
	}
}

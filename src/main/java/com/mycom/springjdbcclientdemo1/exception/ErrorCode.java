package com.mycom.springjdbcclientdemo1.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * รวมรหัสข้อผิดพลาดทางธุรกิจ (business error) ไว้ที่เดียว
 *
 * แต่ละรายการกำหนด 3 อย่างคู่กันเสมอ
 *   httpStatus     : HTTP status ที่ตอบกลับ (404, 400, 500 ...)
 *   code           : รหัสธุรกิจแบบ String ที่ client ใช้แยกกรณี เช่น "ORD-001"
 *   defaultMessage : ข้อความเริ่มต้น (ส่งข้อความเฉพาะกรณีแทนได้ตอน throw)
 *
 * ทำไมใช้ enum แทนการพิมพ์ String เองทุกครั้ง
 *   - พิมพ์ผิดไม่ได้ (compiler ตรวจให้) และ IDE ช่วยเติมชื่อให้
 *   - รหัสเดียวกันได้ HTTP status เดียวกันทุกครั้ง ไม่ต้องจำคู่กันเอง
 *   - เปิดไฟล์นี้ไฟล์เดียวก็เห็นรหัสทั้งหมดของระบบ ใช้ทำเอกสารให้ทีม frontend/mobile ได้เลย
 *
 * หลักตั้งรหัส: <กลุ่มงาน>-<เลข 3 หลัก> เช่น COM = ทั่วไป, ORD = order
 * รหัสที่ประกาศใช้แล้ว ห้ามเปลี่ยนความหมาย เพราะ client อาจเขียนโค้ดผูกกับรหัสไว้
 */
@Getter
public enum ErrorCode {

	// ---- ทั่วไป (COM) ----
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COM-001", "ข้อมูลที่ส่งมาไม่ถูกต้อง"),
	REQUEST_REJECTED(HttpStatus.BAD_REQUEST, "COM-002", "คำขอไม่ถูกต้อง"),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COM-999", "เกิดข้อผิดพลาดภายในระบบ กรุณาติดต่อผู้ดูแลระบบ"),

	// ---- order (ORD) ----
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD-001", "ไม่พบรายการสั่งซื้อ"),

	// ---- ตัวอย่างใน HelloWorldController (DEMO) ----
	NAME_NOT_ALLOWED(HttpStatus.NOT_FOUND, "DEMO-001", "ชื่อนี้ไม่ได้รับอนุญาต");

	private final HttpStatus httpStatus;
	private final String code;
	private final String defaultMessage;

	ErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.defaultMessage = defaultMessage;
	}
}

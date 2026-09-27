package com.mycom.springjdbcclientdemo1.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustOrder {

	private Integer orderId;
	private String customerName;
	private BigDecimal totalAmount;

	@JsonFormat(pattern = "yyyy-MM-dd") // กำหนดรูปแบบวันที่ในการแสดงผล
	private LocalDate orderDate;

	/*
	 * Instant = จุดเวลาที่แน่นอนบนเส้นเวลา (ไม่ผูกกับ timezone ใด) ต่างจาก LocalDateTime ที่เป็นแค่
	 * "วันที่+เวลาบนนาฬิกา" โดยไม่รู้ว่าเป็นเวลาของประเทศไหน
	 * JSON ต้องมี offset เสมอ เช่น 2026-09-05T19:41:30.085+07:00 (ส่งเข้าหรือแสดงผล)
	 * timezone = "Asia/Bangkok" ทำให้แสดงผลเป็นเวลาไทย
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Bangkok")
	private Instant insertDatetime;

}

package com.mycom.springjdbcclientdemo1.config;

import java.time.ZoneId;

/**
 * ค่าคงที่ timezone ของระบบ (Asia/Bangkok)
 *
 * - ภายในโปรแกรมใช้ Instant ทั้งหมด (จุดเวลาแบบ UTC ไม่ขึ้นกับ timezone)
 * - คอลัมน์ DATETIME ใน database เก็บเป็น "เวลาท้องถิ่น Asia/Bangkok"
 *   โดยให้ MariaDB driver แปลง Instant <-> DATETIME ตาม connectionTimeZone=Asia/Bangkok
 *   (กำหนดไว้ใน jdbc-url ของ application-dev.yml)
 */
public final class AppTimeZone {

	public static final String ZONE_ID_STR = "Asia/Bangkok";
	public static final ZoneId ZONE_ID = ZoneId.of(ZONE_ID_STR);

	private AppTimeZone() {
	}
}

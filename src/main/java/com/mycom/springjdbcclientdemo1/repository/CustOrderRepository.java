package com.mycom.springjdbcclientdemo1.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.springjdbcclientdemo1.config.AppTimeZone;
import com.mycom.springjdbcclientdemo1.model.CustOrder;

/**
 * ตัวอย่างการใช้ JdbcClient (Spring 6.1+ / Spring Boot 3.2+) แทน JdbcTemplate
 *
 * JdbcClient เป็น API แบบ fluent (ต่อคำสั่งเป็นสาย) ที่ข้างในยังใช้ JdbcTemplate
 * และ NamedParameterJdbcTemplate ทำงาน Spring Boot สร้าง bean ให้อัตโนมัติ
 *
 * รูปแบบการเขียน:  jdbcClient.sql(SQL).param(...).query(...).list()/optional()/single()
 *                  jdbcClient.sql(SQL).param(...).update()
 *
 * ข้อดีเทียบกับ JdbcTemplate
 *  - ใช้ชื่อ parameter (:orderId) แทน ? จึงไม่ต้องนับลำดับ ลดโอกาสใส่ค่าสลับกัน
 *  - optional() คืน Optional เมื่อไม่พบข้อมูล (queryForObject ของ JdbcTemplate จะ throw
 *    EmptyResultDataAccessException)
 *  - ไม่ต้องสร้าง new Object[] { ... }
 *
 * ข้อจำกัด: JdbcClient ยังไม่มี batch update จึงยังใช้ JdbcTemplate ใน insertUsersByBatch()
 */
@Repository
public class CustOrderRepository {

	private final JdbcClient jdbcClient;

	public CustOrderRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public int insert(CustOrder custorder) {

		String sql = """
				INSERT INTO cust_order
				(order_id, customer_name, total_amount, order_date, insert_datetime)
				VALUES (:orderId, :customerName, :totalAmount, :orderDate, :insertDatetime)
				""";

		// .paramSource(custorder) ดึงค่าจาก getter ที่ชื่อตรงกับ :parameter ให้อัตโนมัติ
		// เช่น :orderId -> getOrderId() เขียนสั้นลงแต่ต้องตั้งชื่อ parameter ให้ตรงกับ field
		// หมายเหตุ: insertDatetime ถูกส่งเป็น Instant ตรงๆ (setObject) ได้เพราะไดรเวอร์ MariaDB 3.x รองรับ
		// คอลัมน์ต้องเป็น DATETIME(6) จึงจะเก็บเศษวินาทีได้ครบ (DATETIME เฉยๆ จะตัดทิ้ง)
		// DATETIME ไม่เก็บ timezone ไดรเวอร์จึงแปลงตาม connectionTimeZone=Asia/Bangkok ที่ตั้งไว้ใน datasource url
		return jdbcClient.sql(sql)
				.paramSource(custorder)
				.update();
	}

	public List<CustOrder> findAll() {
		String sql = "SELECT * FROM cust_order ORDER BY order_id";
		return jdbcClient.sql(sql)
				.query(CustOrder.class)
				.list(); // หลายแถว -> List (ไม่พบ = list ว่าง)
	}

	/**
	 * คืน Optional แทน null หรือ exception
	 * ผู้เรียกต้องจัดการกรณี "ไม่พบ" เอง เช่น orElseThrow(...) หรือ ifPresent(...)
	 */
	public Optional<CustOrder> findById(int orderId) {
		String sql = "SELECT * FROM cust_order WHERE order_id = :orderId";
		return jdbcClient.sql(sql)
				.param("orderId", orderId)
				.query(CustOrder.class)
				.optional(); // 0 แถว -> Optional.empty(), 1 แถว -> Optional.of(...)
	}

	public List<CustOrder> findByCustomerName(String customerName) {
		String sql = "SELECT * FROM cust_order WHERE customer_name = :customerName ORDER BY order_id";
		return jdbcClient.sql(sql)
				.param("customerName", customerName)
				.query(CustOrder.class)
				.list();
	}

	public int update(CustOrder custorder) {

		String sql = """
				UPDATE cust_order SET
				  customer_name = :customerName,
				  total_amount = :totalAmount,
				  order_date = :orderDate,
				  insert_datetime = :insertDatetime
				WHERE order_id = :orderId
				""";

		// .paramSource(custorder) ดึงค่าจาก getter ตามชื่อ :parameter (เหมือน insert)
		return jdbcClient.sql(sql)
				.paramSource(custorder)
				.update(); // คืนจำนวนแถวที่ถูกแก้ไข
	}

	public int deleteById(int orderId) {
		String sql = "DELETE FROM cust_order WHERE order_id = :orderId";
		return jdbcClient.sql(sql)
				.param("orderId", orderId)
				.update();
	}

	/**
	 * ตัวอย่าง insert หลายรายการ
	 * JdbcClient ไม่มี batchUpdate โดยตรง จึงวน insert ภายใน transaction เดียวกัน
	 * (ทั้งหมดสำเร็จ หรือ rollback ทั้งหมด)
	 */
	@Transactional
	public int insertOrdersByBatch(int start, int size) {
		Instant now = Instant.now();
		LocalDate today = LocalDate.now(AppTimeZone.ZONE_ID);
		int total = 0;
		for (int i = 0; i < size; i++) {
			int idx = start + i;
			total += insert(new CustOrder()
					.setOrderId(idx)
					.setCustomerName("customer_name" + idx)
					.setTotalAmount(new BigDecimal(idx + "00"))
					.setOrderDate(today)
					.setInsertDatetime(now));
		}
		return total;
	}

}

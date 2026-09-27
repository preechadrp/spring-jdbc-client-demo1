package com.mycom.springjdbcclientdemo1.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
	private final JdbcTemplate jdbcTemplate; // ใช้เฉพาะงาน batch

	public CustOrderRepository(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
	}

	public void insert(CustOrder custorder) {

		String sql = """
				INSERT INTO cust_order
				(order_id, customer_name, total_amount, order_date, insert_datetime)
				VALUES (:orderId, :customerName, :totalAmount, :orderDate, :insertDatetime)
				""";

		// .paramSource(custorder) ดึงค่าจาก getter ที่ชื่อตรงกับ :parameter ให้อัตโนมัติ
		// เช่น :orderId -> getOrderId() เขียนสั้นลงแต่ต้องตั้งชื่อ parameter ให้ตรงกับ field
		// หมายเหตุ: insertDatetime จะถูกส่งเป็น Instant ตรงๆ (ไม่ผ่าน toTimestamp) ไดรเวอร์ MariaDB 3.x รองรับ
		jdbcClient.sql(sql)
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

	public void insertUsersByBatch() {
		//=== ตัวอย่างการ insert แบบ batch (JdbcClient ยังไม่รองรับ จึงใช้ JdbcTemplate)
		String sql = """
				INSERT INTO cust_order
				(order_id, customer_name, total_amount, order_date, insert_datetime)
				VALUES (?,?,?,?,?)
				""";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			int start = 2011;

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				int idx = start + i;

				ps.setInt(1, idx); //order_id
				ps.setString(2, "customer_name" + idx); //customer_name
				ps.setBigDecimal(3, new BigDecimal(idx + "00")); //total_amount
				ps.setDate(4, java.sql.Date.valueOf(LocalDate.now())); //order_date (แก้จาก index 3 ที่ซ้ำ)
				ps.setTimestamp(5, Timestamp.from(Instant.now())); //insert_datetime (แก้จาก 4)
			}

			@Override
			public int getBatchSize() {
				return 10;
			}

		});
	}

}

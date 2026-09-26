package com.mycom.springjdbcclientdemo1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mycom.springjdbclientdemo1.component.AppConfig;
import com.mycom.springjdbclientdemo1.model.CustOrder;
import com.mycom.springjdbclientdemo1.repository.CustOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustOrderTest {

	@Autowired
	public CustOrderRepository custOrderRepository;

	private final int start = 2011;
	private final int to = start + 5;

	@Test
	@Order(1)
	void delete() {

		//แบบไม่ต้องใส่ตอน constructor class ทดสอบแสดงข้อมูลจากการใช้ @DependsOn("AppConfig") 
		log.info("AppConfig.getInstance().getName() = {}", AppConfig.getInstance().getName());

		log.info("====== start delete");
		for (int idx = start; idx < to; idx++) {
			int eff_row = custOrderRepository.deleteById(idx);
			log.info("delete idx={}, effect row={}", idx, eff_row);
		}
	}

	@Test
	@Order(2)
	void insert() {
		log.info("====== start insert");
		for (int idx = start; idx < to; idx++) {
			var custorder = new CustOrder()
					.setOrderId(idx)
					.setCustomerName("customer_name" + idx)
					.setTotalAmount(new BigDecimal(idx + "00"))
					.setOrderDate(LocalDate.now())
					.setInsertDatetime(Instant.now());

			custOrderRepository.insert(custorder);
		}
	}

	@Test
	@Order(3)
	void findAll() {
		log.info("====== start findAll");
		var datas = custOrderRepository.findAll();
		for (CustOrder custOrder : datas) {
			log.info("custOrder={}", custOrder.toString());
		}
	}

	@Test
	@Order(4)
	void findById() {
		log.info("====== start findById");
		// findById คืน Optional จึงใช้ ifPresentOrElse แทนการเช็ค null
		custOrderRepository.findById(this.start).ifPresentOrElse(
				data -> log.info("custOrder={}", data.toString()),
				() -> log.info("Not found data."));
	}

	@Test
	@Order(5)
	void findByCustomerName() {
		log.info("====== start findByCustomerName");
		var datas = custOrderRepository.findByCustomerName("customer_name" + start);
		if (datas.size() == 0) {
			log.info("Not found data.");
			return;
		}
		for (CustOrder data : datas) {
			log.info("custOrder={}", data.toString());
		}
	}

	@Test
	@Order(6)
	void update() {
		log.info("====== start update");
		var found = custOrderRepository.findById(this.start);
		if (found.isPresent()) {
			var data = found.get();
			data.setTotalAmount(new BigDecimal("200"));
			custOrderRepository.update(data);
			log.info("update={}", data.toString());
		} else {
			log.info("Not found data.");
		}
	}
}

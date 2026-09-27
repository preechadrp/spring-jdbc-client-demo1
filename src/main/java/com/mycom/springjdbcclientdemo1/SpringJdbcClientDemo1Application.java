package com.mycom.springjdbcclientdemo1;

import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mycom.springjdbcclientdemo1.model.CustOrder;
import com.mycom.springjdbcclientdemo1.repository.CustOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class SpringJdbcClientDemo1Application {

	public static void main(String[] args) {
		// Instant <-> DATETIME ใช้ timezone ของ JVM ในการแปลง จึงล็อกเป็นเวลาไทยก่อน Spring start
		// ไม่งั้นผลจะขึ้นกับเครื่องที่รัน (เช่น server/docker ที่เป็น UTC จะเก็บเวลาช้าไป 7 ชั่วโมง) 
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Bangkok"));
		SpringApplication.run(SpringJdbcClientDemo1Application.class, args);
	}

	@Bean
	CommandLineRunner custOrder(CustOrderRepository custOrderRepository) {
		return (args) -> {
			log.info("Running CommandLineRunner.....test");
			var datas = custOrderRepository.findByCustomerName("customer_name" + 2011);
			if (datas.size() == 0) {
				log.info("Not found data.");
				return;
			}
			for (CustOrder data : datas) {
				log.info("custOrder={}", data.toString());
			}
		};
	}

}

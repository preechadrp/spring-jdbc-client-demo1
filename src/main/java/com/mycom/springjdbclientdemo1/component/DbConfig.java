package com.mycom.springjdbclientdemo1.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@DependsOn("AppConfig") //บอกให้สร้าง AppConfig ก่อนสร้าง CommonConfig
@Component
@Data
@ConfigurationProperties(prefix = "app-config.db")
@Slf4j
public class DbConfig {

	private String db1Url;
	private String db1DriverClassName;
	private String db1Username;
	private String db1Password;

	@PostConstruct
	public void init() {
		log.info("===== DbConfig =====");
		log.info("DB1 URL = {}", db1Url);
		log.info("DB1 DriverClassName = {}", db1DriverClassName);
		log.info("DB1 Username = {}", db1Username);
		log.info("DB1 Password = {}", db1Password);

		//แบบไม่ต้องใส่ตอน constructor class ทดสอบแสดงข้อมูลจากการใช้ @DependsOn("AppConfig") 
		log.info("AppConfig.getInstance().getName() = {}", AppConfig.getInstance().getName());
	}

	@PreDestroy
	public void stop() {
		log.info("@PreDestroy");
	}
}
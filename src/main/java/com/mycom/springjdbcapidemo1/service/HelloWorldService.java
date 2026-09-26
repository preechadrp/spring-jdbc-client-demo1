package com.mycom.springjdbcapidemo1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mycom.springjdbcapidemo1.component.AppConfig;
import com.mycom.springjdbcapidemo1.exception.CustomException;
import com.mycom.springjdbcapidemo1.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HelloWorldService {

	//private static final Logger log = LoggerFactory.getLogger(HelloWorldService.class);

	@Value("${app-config.main.name:}")
	private String mainName;

	private final AppConfig appConfig;

	//ใช้ constructor เพื่อเรียก bean ตัวอื่นมาใช้งาน
	public HelloWorldService(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public String hello() {
		try {
			//....todo something
			log.info("HelloWorldService.hello() called");
			log.info("mainName = {}", mainName);
			log.info("this.appConfig.getName() = {}", this.appConfig.getName());

			//แบบไม่ต้องใส่ตอน constructor class
			log.info("AppConfig.getInstance().getName() = {}", AppConfig.getInstance().getName());

			return "Hello World!";
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			// ส่ง e เป็น cause เพื่อให้ log แสดง stack trace ของต้นเหตุจริง
			throw new CustomException(ErrorCode.INTERNAL_ERROR, e.getMessage(), e);
		}
	}
}
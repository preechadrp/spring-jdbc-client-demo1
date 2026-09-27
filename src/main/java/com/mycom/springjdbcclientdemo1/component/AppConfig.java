package com.mycom.springjdbcclientdemo1.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component("AppConfig")
@Getter
@Slf4j
public class AppConfig {

	/*
	 * ตัวอย่างการอ่าน config แบบ @Value("${....}")
	 */

	private static AppConfig instance;

	@Value("${app-config.main.name:}") //ถ้าไม่พบ config นี้ให้ใส่เป็นค่าว่างๆ  (ค่าหลังเครื่อง :)
	private String name;

	@Value("${app-config.main.proxy-host}") //ถ้าไม่มี config นี้จะ error ทันที
	private String proxyHost;

	@Value("${app-config.main.proxy-port:0}") //ถ้าไม่พบ config นี้ให้ใส่เป็น 0
	private Integer proxyPort;

	@Value("${app-config.main.proxy-username}")
	private String proxyUsername;

	@Value("${app-config.main.proxy-password}")
	private String proxyPassword;

	@PostConstruct
	public void init() {
		instance = this;//ช่วยให้ code แบบเก่าสามารถดึงไปใช้งานโดยไม่ต้องใช้หลักการ injection ของ spring

		log.info("==== AppConfig ====");
		log.info("name = {}", instance.getName());
		log.info("proxyHost = {}", instance.getProxyHost());
		log.info("proxyPort = {}", instance.getProxyPort());
		log.info("proxyUsername = {}", instance.getProxyUsername());
		log.info("proxyPassword = {}", instance.getProxyPassword());

	}

	public static AppConfig getInstance() {
		//ตัวอย่างการใช้งานเช่น   AppConfig.getInstance().getName(); เป็นต้น
		return instance;
	}
}

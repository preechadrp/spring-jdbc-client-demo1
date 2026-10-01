package com.mycom.springjdbcclientdemo1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.springjdbcclientdemo1.dto.UserRequest;
import com.mycom.springjdbcclientdemo1.exception.CustomException;
import com.mycom.springjdbcclientdemo1.exception.ErrorCode;
import com.mycom.springjdbcclientdemo1.exception.ExternalApiException;
import com.mycom.springjdbcclientdemo1.service.HelloWorldService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

@Slf4j
@RestController
public class HelloWorldController {

	private final HelloWorldService helloWorldService;

	public HelloWorldController(HelloWorldService helloWorldService) {
		this.helloWorldService = helloWorldService;
	}

	@GetMapping("/hello1")
	public String hello1() {
		//return "Hello World!"; 
		return this.helloWorldService.hello();
	}

	@PostMapping("/hello2")
	public String hello2() {
		return "Hello World2!";
	}

	@GetMapping("/user")
	public UserRequest getUser() {
		return new UserRequest("นายใจดี", "Joe@email.com", "1234");
	}

	@PostMapping("/user")
	public UserRequest postUser(@RequestBody @Valid UserRequest userDto) {
		return userDto;
	}

	@PostMapping("/user-jsonnode")
	public JsonNode postUserByJsonnode(@RequestBody JsonNode data) {
		log.info("jsonNode : {}", data.toPrettyString());
		return data;
	}

	@GetMapping("/custom-exception")
	public String testCustomException() {
		String name = "Joe";
		if (!"abc".equals(name)) {
			//throw new CustomException(ErrorCode.NAME_NOT_ALLOWED); // ใช้ข้อความเริ่มต้นใน ErrorCode
			//throw new CustomException(ErrorCode.NAME_NOT_ALLOWED, "abc is not allowed", true); // พิมพ์ stack trace ใน log
			throw new CustomException(ErrorCode.NAME_NOT_ALLOWED, "abc is not allowed");
		}
		return "custom-exception";
	}

	/**
	 * ตัวอย่างจำลองกรณี external API ตอบ error
	 * ในงานจริงให้ throw จุดนี้จาก service หลังอ่าน errorCode/errorMessage ของระบบปลายทางแล้ว
	 */
	@GetMapping("/external-api-error")
	public String testExternalApiException() {
		throw new ExternalApiException(
				ErrorCode.EXTERNAL_API_REJECTED,
				"payment-service",
				422,
				"EXT-005",
				"user ยกเลิกการกรอก OTP",
				true,
				new IllegalStateException("demo upstream error"));
	}

}

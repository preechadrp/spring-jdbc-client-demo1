package com.mycom.springjdbcapidemo1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
	@NotBlank(message = "กรุณากรอกชื่อผู้ใช้")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "ชื่อผู้ใช้ต้องเป็นภาษาอังกฤษ (a-z, A-Z) เท่านั้น")
	private String userName;

	@NotBlank(message = "กรุณากรอกอีเมล")
	@Email(message = "รูปแบบอีเมลไม่ถูกต้อง")
	private String email;

	@NotBlank(message = "กรุณากรอกรหัสผ่าน")
	@Size(min = 8, message = "รหัสผ่านต้องมีความยาวอย่างน้อย 8 ตัวอักษร")
	String password;
}
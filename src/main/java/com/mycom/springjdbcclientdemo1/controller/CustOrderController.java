package com.mycom.springjdbcclientdemo1.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.springjdbcclientdemo1.dto.CustomerNameRequest;
import com.mycom.springjdbcclientdemo1.exception.CustomException;
import com.mycom.springjdbcclientdemo1.exception.ErrorCode;
import com.mycom.springjdbcclientdemo1.model.CustOrder;
import com.mycom.springjdbcclientdemo1.repository.CustOrderRepository;

@RestController
public class CustOrderController {

	private final CustOrderRepository custOrderRepository;

	public CustOrderController(CustOrderRepository custOrderRepository) {
		this.custOrderRepository = custOrderRepository;
	}

	@GetMapping("/custorder")
	public List<CustOrder> getAllCustOrders() {
		List<CustOrder> custOrders = custOrderRepository.findAll();
		return custOrders;
	}

	@GetMapping("/custorder/id/{orderId}")
	public CustOrder getCustOrderByOrderId(@PathVariable Integer orderId) {
		// findById คืน Optional: ถ้าไม่พบให้ตอบ 404 ผ่าน GlobalExceptionHandler
		return custOrderRepository.findById(orderId)
				.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND, "ไม่พบ order id " + orderId));
	}

	@GetMapping("/custorder/{customerName}")
	public List<CustOrder> getCustOrderByCustomerName(@PathVariable String customerName) {
		return custOrderRepository.findByCustomerName(customerName);
	}

	@PostMapping("/custorder-find-by-customer-name")
	public List<CustOrder> getCustOrderByCustomerNameByPost(@RequestBody CustomerNameRequest customerNameDto) {
		return custOrderRepository.findByCustomerName(customerNameDto.customerName());
	}

	@PostMapping("/custorder")
	public ResponseEntity<CustOrder> createCustOrder(@RequestBody CustOrder custOrder) {
		custOrderRepository.insert(custOrder);
		//return ResponseEntity.ok(custOrder);
		return ResponseEntity.status(HttpStatus.CREATED).body(custOrder);// 201 Created status code
	}
}

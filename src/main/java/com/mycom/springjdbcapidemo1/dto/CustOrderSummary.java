package com.mycom.springjdbcapidemo1.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustOrderSummary {
	private String customerName;
	private BigDecimal totalAmount;
}

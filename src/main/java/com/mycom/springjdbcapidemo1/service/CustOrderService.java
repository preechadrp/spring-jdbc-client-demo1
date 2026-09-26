package com.mycom.springjdbcapidemo1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.springjdbcapidemo1.model.CustOrder;
import com.mycom.springjdbcapidemo1.repository.CustOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustOrderService {

	private final CustOrderRepository custOrderRepository;

	public CustOrderService(CustOrderRepository custOrderRepository) {
		this.custOrderRepository = custOrderRepository;
	}

	@Transactional(rollbackFor = Exception.class) //เพื่อให้ db commit
	public void insert(CustOrder custOrder) {
		this.custOrderRepository.insert(custOrder);

		//      กรณีต้องการให้ rollbak แบบ manaul ไม่ต้องสร้าง exception 
		//        if (somethingWrong) {
		//
		//            TransactionAspectSupport
		//                .currentTransactionStatus()
		//                .setRollbackOnly();
		//
		//            return;
		//        }
	}
}

package com.mycom.springjdbcclientdemo1.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.mycom.springjdbcclientdemo1.model.CustOrder;
import com.mycom.springjdbcclientdemo1.repository.CustOrderRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * ตัวอย่างคุม transaction ด้วย PlatformTransactionManager ในโค้ด (programmatic) แทน @Transactional
 * (เทียบกับ CustOrderService ที่ใช้ @Transactional)
 *
 * PlatformTransactionManager มาจากไหน: Spring Boot สร้าง bean ชื่อ transactionManager
 * (JdbcTransactionManager) จาก spring.datasource ให้อัตโนมัติ inject มาใช้ได้เลยไม่ต้องสร้าง config เอง
 * (Spring Boot สร้าง bean TransactionTemplate ให้ด้วย inject มาตรงๆ ก็ได้
 *  แต่ในตัวอย่างนี้สร้างเองเพื่อให้เห็นว่าตั้งค่า timeout / readOnly อย่างไร)
 *
 * ทำไมใช้ได้: เมื่อ transaction manager เปิด transaction จะผูก Connection ไว้กับ thread ปัจจุบัน
 * JdbcClient (ที่ใช้ DataSource ตัวเดียวกัน) จึงได้ Connection เดิม -> อยู่ใน transaction เดียวกัน
 *
 * มี 2 แบบ
 *  1. PlatformTransactionManager ตรงๆ : getTransaction() -> commit() / rollback() เขียนเอง
 *  2. TransactionTemplate              : ห่อแบบที่ 1 ไว้ให้ ส่งแค่ lambda (แนะนำ เขียนสั้นและพลาดยากกว่า)
 *
 * ข้อดีเทียบกับ @Transactional
 *  - ไม่ใช้ proxy จึงไม่มีปัญหาเรียก method ในคลาสเดียวกันแล้ว transaction ไม่ทำงาน (self-invocation)
 *  - กำหนดขอบเขต transaction ได้แค่บางส่วนของ method
 *  - rollback แบบ manual ใช้ status.setRollbackOnly() ได้เลย
 *    ไม่ต้องใช้ TransactionAspectSupport.currentTransactionStatus() แบบใน CustOrderService
 */
@Slf4j
@Service
public class CustOrderTxService {

	private final CustOrderRepository custOrderRepository;
	private final PlatformTransactionManager txManager;

	private final TransactionTemplate txTemplate;
	private final TransactionTemplate readOnlyTxTemplate;

	public CustOrderTxService(CustOrderRepository custOrderRepository, PlatformTransactionManager txManager) {
		this.custOrderRepository = custOrderRepository;
		this.txManager = txManager;

		// TransactionTemplate เป็น thread-safe สร้างครั้งเดียวแล้วใช้ซ้ำได้
		this.txTemplate = new TransactionTemplate(txManager);
		this.txTemplate.setTimeout(10); // วินาที

		// เทียบได้กับ @Transactional(readOnly = true)
		this.readOnlyTxTemplate = new TransactionTemplate(txManager);
		this.readOnlyTxTemplate.setReadOnly(true);
	}

	// =====================================================================
	// แบบที่ 1: ใช้ PlatformTransactionManager ตรงๆ
	// =====================================================================

	/**
	 * insert หลายรายการใน transaction เดียว ถ้ารายการไหนพัง (เช่น order_id ซ้ำ) จะ rollback ทั้งหมด
	 * เทียบเท่า @Transactional(rollbackFor = Exception.class)
	 *
	 * @return จำนวนแถวที่ insert
	 */
	public int insertAllManual(List<CustOrder> custOrders) {
		// กำหนดคุณสมบัติของ transaction (เทียบกับ attribute ของ @Transactional)
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("CustOrderTxService.insertAllManual");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		def.setTimeout(10);

		// เริ่ม transaction (ยืม Connection จาก pool แล้ว setAutoCommit(false))
		TransactionStatus status = txManager.getTransaction(def);

		int total = 0;
		try {
			for (CustOrder custOrder : custOrders) {
				total += custOrderRepository.insert(custOrder);
			}
		} catch (RuntimeException | Error ex) {
			log.warn("rollback: {}", ex.getMessage());
			txManager.rollback(status);
			throw ex;
		}
		// commit อยู่นอก try: ถ้า commit เองพัง manager จัดการ rollback ให้แล้ว
		// ไม่ต้อง (และห้าม) เรียก rollback ซ้ำ ไม่งั้นจะได้ IllegalTransactionStateException
		txManager.commit(status);
		return total;
	}

	// =====================================================================
	// แบบที่ 2: ใช้ TransactionTemplate (แนะนำ)
	// =====================================================================

	/**
	 * เหมือน insertAllManual แต่สั้นกว่า
	 * lambda จบปกติ -> commit, โยน RuntimeException/Error -> rollback แล้วโยนต่อ
	 */
	public int insertAll(List<CustOrder> custOrders) {
		return txTemplate.execute(status -> custOrders.stream()
				.mapToInt(custOrderRepository::insert)
				.sum());
	}

	/**
	 * rollback โดยไม่ต้องโยน exception ใช้ status.setRollbackOnly()
	 * ตัวอย่าง: แก้ยอดรวม แต่ถ้ายอดใหม่ต่ำกว่าครึ่งของยอดเดิม ให้ยกเลิก
	 *
	 * @return true = commit, false = rollback (หรือไม่พบ order)
	 */
	public boolean updateTotalAmount(int orderId, BigDecimal newAmount) {
		return txTemplate.execute(status -> {
			CustOrder custOrder = custOrderRepository.findById(orderId).orElse(null);
			if (custOrder == null) {
				return false;
			}

			BigDecimal oldAmount = custOrder.getTotalAmount();
			custOrderRepository.update(custOrder.setTotalAmount(newAmount));

			if (oldAmount != null && newAmount.compareTo(oldAmount.divide(BigDecimal.TWO)) < 0) {
				log.warn("rollback: ยอดใหม่ {} ต่ำกว่าครึ่งของยอดเดิม {}", newAmount, oldAmount);
				status.setRollbackOnly(); // จบ lambda แล้วจะ rollback แทน commit
				return false;
			}
			return true;
		});
	}

	/** transaction แบบ read-only (ถ้าไม่มีค่าคืน ใช้ executeWithoutResult ได้) */
	public List<CustOrder> findAll() {
		return readOnlyTxTemplate.execute(status -> custOrderRepository.findAll());
	}
}

DROP TABLE IF EXISTS `cust_order`;

CREATE TABLE `cust_order` (  
  `order_id` int(11) NOT NULL COMMENT 'เลข order',
  `customer_name` varchar(250) NOT NULL COMMENT 'ชื่อลูกค้า',
  `total_amount` decimal(18,2) DEFAULT NULL COMMENT 'ยอดรวมสินค้า',
  `order_date` date DEFAULT NULL COMMENT 'วันที่สั่งซื้อสินค้า',
  `insert_datetime` datetime(6) DEFAULT NULL COMMENT 'วันที่บันทึกข้อมูล',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB;
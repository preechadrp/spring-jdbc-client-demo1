package com.mycom.springjdbcapidemo1.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CronSchedule {

	@Scheduled(cron = "0/5 * * * * *")
	public void run() {
		try {
			log.info("CronSchedule is running...");
			//..
		} catch (Exception e) {
			log.error("CronSchedule Error : {}", e.getMessage(), e);
		}
	}

	@Scheduled(cron = "* * 0 * * MON-FRI")
	public void run2() {
		try {
			log.info("CronSchedule is running... at 0.00 every day between monday and friday");
			//..
		} catch (Exception e) {
			log.error("CronSchedule Error : {}", e.getMessage(), e);
		}
	}

	@Scheduled(cron = "* * 0 * * MON,WED,FRI")
	//@Scheduled(cron = "${app.schedule.cron-run3}")
	public void run3() {
		try {
			log.info("CronSchedule is running... at 0.00 at monday, wednesday, friday");
			//..
		} catch (Exception e) {
			log.error("CronSchedule Error : {}", e.getMessage(), e);
		}
	}
}

package com.dinstone.loghub.slf4j;

import org.junit.Test;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;

public class LoggerTest {

	@Test
	public void test00() throws InterruptedException {
		Logger logger = LoggerFactory.getLogger(LoggerTest.class);
		for (int i = 0; i < 5; i++) {
			logger.info("case 00 {}", i);
		}

		Thread.sleep(1000);
	}

}

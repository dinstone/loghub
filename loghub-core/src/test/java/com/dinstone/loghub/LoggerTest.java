
package com.dinstone.loghub;

import org.junit.Test;

import com.dinstone.loghub.jul.JulDelegateFactory;
import com.dinstone.loghub.jul.JulOption;

public class LoggerTest {

	@Test
	public void test00() throws InterruptedException {
		Logger logger = LoggerFactory.getLogger(LoggerTest.class);
		for (int i = 0; i < 5; i++) {
			logger.info("case 00 {},{}", i);
		}

		Thread.sleep(1000);
	}

	@Test
	public void test01() throws InterruptedException {
		JulOption option = new JulOption().setPattern("logs/loghub_%d.log");
		JulDelegateFactory factory = new JulDelegateFactory(option);
		LoggerFactory.initialise(factory);

		Logger logger = LoggerFactory.getLogger(LoggerTest.class);
		for (int i = 0; i < 5; i++) {
			logger.info("case 01 {},{}", i);
		}

		Thread.sleep(1000);
	}

}

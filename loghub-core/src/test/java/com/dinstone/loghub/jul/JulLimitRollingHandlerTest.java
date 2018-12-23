package com.dinstone.loghub.jul;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Test;

public class JulLimitRollingHandlerTest {

	@Test
	public void testJulLimitRollingHandlerStringIntInt() throws InterruptedException {
		LogManager.getLogManager().reset();
		Logger logger = Logger.getLogger("");
		try {
			String pattern = "logs/.log";
			if (pattern != null) {
				Handler rollingHandler = new JulLimitRollingHandler(pattern, 1, 100);
				rollingHandler.setFormatter(new JulFormatter());
				rollingHandler.setLevel(Level.INFO);
				logger.addHandler(rollingHandler);
			}
			if (true) {
				ConsoleHandler consoleHandler = new ConsoleHandler();
				consoleHandler.setFormatter(new JulFormatter());
				consoleHandler.setLevel(Level.INFO);
				logger.addHandler(consoleHandler);
			}
		} catch (IOException e) {
			throw new RuntimeException("init jul logger error", e);
		}

		for (int i = 0; i < 5; i++) {
			logger.warning("warn " + i);
		}

		Thread.sleep(1000);
	}

}

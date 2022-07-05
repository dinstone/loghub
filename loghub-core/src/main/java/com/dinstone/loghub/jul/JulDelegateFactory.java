/*
 * Copyright (C) 2018-2022 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dinstone.loghub.jul;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.dinstone.loghub.spi.LogDelegate;
import com.dinstone.loghub.spi.LogDelegateFactory;

public class JulDelegateFactory implements LogDelegateFactory {

	public JulDelegateFactory() {
		if (System.getProperty("java.util.logging.config.file") == null) {
			if (!loadConfigFile()) {
				activateOption(new JulOption().setPattern(null).setConsole(true));
			}
		}
	}

	public JulDelegateFactory(JulOption option) {
		activateOption(option);
	}

	private boolean loadConfigFile() {
		ClassLoader classLoader = JulDelegateFactory.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream("logging.properties")) {
			if (is != null) {
				LogManager.getLogManager().readConfiguration(is);
				return true;
			}
		} catch (IOException ignore) {
		}
		return false;
	}

	private void activateOption(JulOption option) {
		LogManager.getLogManager().reset();
		Logger logger = Logger.getLogger("");
		try {
			String pattern = option.getPattern();
			if (pattern != null) {
				int limitSize = option.getLimitSize();
				int limitDays = option.getLimitDays();
				Handler rollingHandler = new JulDailyRollingHandler(pattern, limitDays, limitSize);
				rollingHandler.setFormatter(option.getFormatter());
				rollingHandler.setLevel(option.getLevel());
				logger.addHandler(rollingHandler);
			}
			if (option.isConsole()) {
				Handler outHandler = new JulConsoleHandler(System.out, option.getFormatter());
				outHandler.setLevel(option.getLevel());
				logger.addHandler(outHandler);
			}
		} catch (IOException e) {
			throw new RuntimeException("init jul logger error", e);
		}
	}

	@Override
	public LogDelegate createDelegate(final String name) {
		return new JulDelegate(name);
	}

}

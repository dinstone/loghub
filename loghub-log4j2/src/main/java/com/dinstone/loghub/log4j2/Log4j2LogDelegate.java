/*
 * Copyright (C) 2018-2019 dinstone<dinstone@163.com>
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
package com.dinstone.loghub.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.ExtendedLogger;

import com.dinstone.loghub.spi.LogDelegate;

/**
 * A {@link LogDelegate} which delegates to Apache Log4j2
 *
 * @author Clement Escoffier - clement@apache.org
 * @author dinstone
 */
public class Log4j2LogDelegate implements LogDelegate {

	private static final String FQCN = com.dinstone.loghub.Logger.class.getCanonicalName();

	final ExtendedLogger logger;

	Log4j2LogDelegate(final String name) {
		logger = (ExtendedLogger) org.apache.logging.log4j.LogManager.getLogger(name);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void fatal(final Object message) {
		log(Level.FATAL, message);
	}

	@Override
	public void fatal(final Object message, final Throwable t) {
		log(Level.FATAL, message, t);
	}

	@Override
	public void error(final Object message) {
		log(Level.ERROR, message);
	}

	@Override
	public void error(Object message, Object... params) {
		log(Level.ERROR, message.toString(), params);
	}

	@Override
	public void error(final Object message, final Throwable t) {
		log(Level.ERROR, message, t);
	}

	@Override
	public void error(Object message, Throwable t, Object... params) {
		log(Level.ERROR, message.toString(), t, params);
	}

	@Override
	public void warn(final Object message) {
		log(Level.WARN, message);
	}

	@Override
	public void warn(Object message, Object... params) {
		log(Level.WARN, message.toString(), params);
	}

	@Override
	public void warn(final Object message, final Throwable t) {
		log(Level.WARN, message, t);
	}

	@Override
	public void warn(Object message, Throwable t, Object... params) {
		log(Level.WARN, message.toString(), t, params);
	}

	@Override
	public void info(final Object message) {
		log(Level.INFO, message);
	}

	@Override
	public void info(Object message, Object... params) {
		log(Level.INFO, message.toString(), params);
	}

	@Override
	public void info(final Object message, final Throwable t) {
		log(Level.INFO, message, t);
	}

	@Override
	public void info(Object message, Throwable t, Object... params) {
		log(Level.INFO, message.toString(), t, params);
	}

	@Override
	public void debug(final Object message) {
		log(Level.DEBUG, message);
	}

	@Override
	public void debug(Object message, Object... params) {
		log(Level.DEBUG, message.toString(), params);
	}

	@Override
	public void debug(final Object message, final Throwable t) {
		log(Level.DEBUG, message, t);
	}

	@Override
	public void debug(Object message, Throwable t, Object... params) {
		log(Level.DEBUG, message.toString(), t, params);
	}

	@Override
	public void trace(final Object message) {
		log(Level.TRACE, message);
	}

	@Override
	public void trace(Object message, Object... params) {
		log(Level.TRACE, message.toString(), params);
	}

	@Override
	public void trace(final Object message, final Throwable t) {
		log(Level.TRACE, message.toString(), t);
	}

	@Override
	public void trace(Object message, Throwable t, Object... params) {
		log(Level.TRACE, message.toString(), t, params);
	}

	private void log(Level level, Object message) {
		log(level, message, null);
	}

	private void log(Level level, Object message, Throwable t) {
		if (message instanceof Message) {
			logger.logIfEnabled(FQCN, level, null, (Message) message, t);
		} else {
			logger.logIfEnabled(FQCN, level, null, message, t);
		}
	}

	private void log(Level level, String message, Object... params) {
		logger.logIfEnabled(FQCN, level, null, message, params);
	}

	private void log(Level level, String message, Throwable t, Object... params) {
		logger.logIfEnabled(FQCN, level, null, new FormattedMessage(message, params), t);
	}

}
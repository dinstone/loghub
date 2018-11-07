/*
 * Copyright (C) 2017-2018 dinstone<dinstone@163.com>
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
package com.dinstone.loghub.log4j;

import org.apache.log4j.Level;

import com.dinstone.loghub.spi.LogDelegate;

/**
 * A {@link LogDelegate} which delegates to Apache Log4j
 *
 * @author <a href="kenny.macleod@kizoom.com">Kenny MacLeod</a>
 * @author dinstone
 */
public class Log4jLogDelegate implements LogDelegate {

	private static final String FQCN = com.dinstone.loghub.Logger.class.getCanonicalName();

	private final org.apache.log4j.Logger logger;

	Log4jLogDelegate(final String name) {
		logger = org.apache.log4j.Logger.getLogger(name);
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public void fatal(final Object message) {
		log(Level.FATAL, message);
	}

	public void fatal(final Object message, final Throwable t) {
		log(Level.FATAL, message, t);
	}

	public void error(final Object message) {
		log(Level.ERROR, message);
	}

	@Override
	public void error(Object message, Object... params) {
		throwUnsupportedOperationException();
	}

	public void error(final Object message, final Throwable t) {
		log(Level.ERROR, message, t);
	}

	@Override
	public void error(Object message, Throwable t, Object... params) {
		throwUnsupportedOperationException();
	}

	public void warn(final Object message) {
		log(Level.WARN, message);
	}

	@Override
	public void warn(Object message, Object... params) {
		throwUnsupportedOperationException();
	}

	public void warn(final Object message, final Throwable t) {
		log(Level.WARN, message, t);
	}

	@Override
	public void warn(Object message, Throwable t, Object... params) {
		throwUnsupportedOperationException();
	}

	public void info(final Object message) {
		log(Level.INFO, message);
	}

	@Override
	public void info(Object message, Object... params) {
		throwUnsupportedOperationException();
	}

	public void info(final Object message, final Throwable t) {
		log(Level.INFO, message, t);
	}

	@Override
	public void info(Object message, Throwable t, Object... params) {
		throwUnsupportedOperationException();
	}

	public void debug(final Object message) {
		log(Level.DEBUG, message);
	}

	@Override
	public void debug(Object message, Object... params) {
		throwUnsupportedOperationException();
	}

	public void debug(final Object message, final Throwable t) {
		log(Level.DEBUG, message, t);
	}

	@Override
	public void debug(Object message, Throwable t, Object... params) {
		throwUnsupportedOperationException();
	}

	public void trace(final Object message) {
		log(Level.TRACE, message);
	}

	@Override
	public void trace(Object message, Object... params) {
		throwUnsupportedOperationException();
	}

	public void trace(final Object message, final Throwable t) {
		log(Level.TRACE, message, t);
	}

	@Override
	public void trace(Object message, Throwable t, Object... params) {
		throwUnsupportedOperationException();
	}

	private void log(Level level, Object message) {
		log(level, message, null);
	}

	private void log(Level level, Object message, Throwable t) {
		logger.log(FQCN, level, message, t);
	}

	private void throwUnsupportedOperationException() {
		throw new UnsupportedOperationException("Log4j version used in loghub doesn't support parameterized logging.");
	}

}

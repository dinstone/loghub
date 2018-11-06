package com.dinstone.loghub.slf4j;

import static org.slf4j.spi.LocationAwareLogger.DEBUG_INT;
import static org.slf4j.spi.LocationAwareLogger.ERROR_INT;
import static org.slf4j.spi.LocationAwareLogger.INFO_INT;
import static org.slf4j.spi.LocationAwareLogger.TRACE_INT;
import static org.slf4j.spi.LocationAwareLogger.WARN_INT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import com.dinstone.loghub.spi.LogDelegate;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author dinstone
 */
public class Slf4jLogDelegate implements LogDelegate {

	private static final String FQCN = com.dinstone.loghub.Logger.class.getCanonicalName();

	private final Logger logger;

	Slf4jLogDelegate(final String name) {
		logger = LoggerFactory.getLogger(name);
	}

	public Slf4jLogDelegate(Object logger) {
		this.logger = (Logger) logger;
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
		log(ERROR_INT, message);
	}

	public void fatal(final Object message, final Throwable t) {
		log(ERROR_INT, message, t);
	}

	public void error(final Object message) {
		log(ERROR_INT, message);
	}

	@Override
	public void error(Object message, Object... params) {
		log(ERROR_INT, message, null, params);
	}

	public void error(final Object message, final Throwable t) {
		log(ERROR_INT, message, t);
	}

	@Override
	public void error(Object message, Throwable t, Object... params) {
		log(ERROR_INT, message, t, params);
	}

	public void warn(final Object message) {
		log(WARN_INT, message);
	}

	@Override
	public void warn(Object message, Object... params) {
		log(WARN_INT, message, null, params);
	}

	public void warn(final Object message, final Throwable t) {
		log(WARN_INT, message, t);
	}

	@Override
	public void warn(Object message, Throwable t, Object... params) {
		log(WARN_INT, message, t, params);
	}

	public void info(final Object message) {
		log(INFO_INT, message);
	}

	@Override
	public void info(Object message, Object... params) {
		log(INFO_INT, message, null, params);
	}

	public void info(final Object message, final Throwable t) {
		log(INFO_INT, message, t);
	}

	@Override
	public void info(Object message, Throwable t, Object... params) {
		log(INFO_INT, message, t, params);
	}

	public void debug(final Object message) {
		log(DEBUG_INT, message);
	}

	public void debug(final Object message, final Object... params) {
		log(DEBUG_INT, message, null, params);
	}

	public void debug(final Object message, final Throwable t) {
		log(DEBUG_INT, message, t);
	}

	public void debug(final Object message, final Throwable t, final Object... params) {
		log(DEBUG_INT, message, t, params);
	}

	public void trace(final Object message) {
		log(TRACE_INT, message);
	}

	@Override
	public void trace(Object message, Object... params) {
		log(TRACE_INT, message, null, params);
	}

	public void trace(final Object message, final Throwable t) {
		log(TRACE_INT, message, t);
	}

	@Override
	public void trace(Object message, Throwable t, Object... params) {
		log(TRACE_INT, message, t, params);
	}

	private void log(int level, Object message) {
		log(level, message, null);
	}

	private void log(int level, Object message, Throwable t) {
		log(level, message, t, (Object[]) null);
	}

	private void log(int level, Object message, Throwable t, Object... params) {
		String msg = (message == null) ? "NULL" : message.toString();

		// We need to compute the right parameters.
		// If we have both parameters and an error, we need to build a new array
		// [params, t]
		// If we don't have parameters, we need to build a new array [t]
		// If we don't have error, it's just params.
		Object[] parameters = params;
		if (params != null && t != null) {
			parameters = new Object[params.length + 1];
			System.arraycopy(params, 0, parameters, 0, params.length);
			parameters[params.length] = t;
		} else if (params == null && t != null) {
			parameters = new Object[] { t };
		}

		if (logger instanceof LocationAwareLogger) {
			// make sure we don't format the objects if we don't log the line anyway
			if (level == TRACE_INT && logger.isTraceEnabled() || level == DEBUG_INT && logger.isDebugEnabled()
					|| level == INFO_INT && logger.isInfoEnabled() || level == WARN_INT && logger.isWarnEnabled()
					|| level == ERROR_INT && logger.isErrorEnabled()) {
				LocationAwareLogger l = (LocationAwareLogger) logger;
				FormattingTuple ft = MessageFormatter.arrayFormat(msg, parameters);
				l.log(null, FQCN, level, ft.getMessage(), null, ft.getThrowable());
			}
		} else {
			switch (level) {
			case TRACE_INT:
				logger.trace(msg, parameters);
				break;
			case DEBUG_INT:
				logger.debug(msg, parameters);
				break;
			case INFO_INT:
				logger.info(msg, parameters);
				break;
			case WARN_INT:
				logger.warn(msg, parameters);
				break;
			case ERROR_INT:
				logger.error(msg, parameters);
				break;
			default:
				throw new IllegalArgumentException("Unknown log level " + level);
			}
		}
	}

}
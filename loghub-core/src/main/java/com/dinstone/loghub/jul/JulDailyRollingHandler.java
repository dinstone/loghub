
package com.dinstone.loghub.jul;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * Simple daily rolling file handler
 *
 * @author dinstone
 */
public class JulDailyRollingHandler extends StreamHandler {

	private static final String DEFAULT_LOG_FILE_PATTERN = "logs/jul_%d.log";

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private final RollingCalendar calendar = new RollingCalendar();

	private final Date nowDate = new Date();

	private FileOutputStream fos;

	private String pattern;

	private long nextCheck;

	public JulDailyRollingHandler() throws IOException {
		configure();
		activate();
	}

	public JulDailyRollingHandler(String pattern) throws IOException {
		if (pattern == null || pattern.length() < 1) {
			throw new IllegalArgumentException("pattern is empty");
		}

		configure();

		this.pattern = pattern;

		activate();
	}

	private void configure() {
		String cname = getClass().getName();
		LogManager logManager = LogManager.getLogManager();
		pattern = logManager.getProperty(cname + ".pattern");
		if (pattern == null) {
			pattern = DEFAULT_LOG_FILE_PATTERN;
		}

		setLevel(getPropertyLevel(cname + ".level"));
		setFilter(getPropertyFilter(cname + ".filter"));
		setFormatter(getPropertyFormatter(cname + ".formatter"));
		try {
			setEncoding(logManager.getProperty(cname + ".encoding"));
		} catch (Exception ex) {
			try {
				setEncoding(null);
			} catch (Exception ex2) {
			}
		}
	}

	private Formatter getPropertyFormatter(String formatteKey) {
		LogManager logManager = LogManager.getLogManager();
		String val = logManager.getProperty(formatteKey);
		try {
			if (val != null) {
				Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
				return (Formatter) clz.newInstance();
			}
		} catch (Exception ex) {
			// We got one of a variety of exceptions in creating the
			// class or creating an instance.
			// Drop through.
		}
		// We got an exception. Return the defaultValue.
		return new JulFormatter();
	}

	private Filter getPropertyFilter(String filterKey) {
		LogManager logManager = LogManager.getLogManager();
		String val = logManager.getProperty(filterKey);
		try {
			if (val != null) {
				Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
				return (Filter) clz.newInstance();
			}
		} catch (Exception ex) {
			// We got one of a variety of exceptions in creating the
			// class or creating an instance.
			// Drop through.
		}
		// We got an exception. Return the defaultValue.
		return null;
	}

	private Level getPropertyLevel(String levelKey) {
		LogManager logManager = LogManager.getLogManager();
		String levelVaule = logManager.getProperty(levelKey);
		if (levelVaule == null) {
			return Level.ALL;
		}
		Level level = Level.parse(levelVaule.trim());
		return level != null ? level : Level.ALL;
	}

	private void activate() throws IOException {
		if (!pattern.contains("%d")) {
			pattern += "%d";
		}

		nextCheck = calendar.nextCheckDate(nowDate).getTime();

		rolling();
	}

	@Override
	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}

		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis >= nextCheck) {
			nowDate.setTime(currentTimeMillis);
			nextCheck = calendar.nextCheckDate(nowDate).getTime();
			try {
				rolling();
			} catch (IOException ioe) {
				if (ioe instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
			}
		}

		super.publish(record);
		super.flush();
	}

	private void rolling() throws IOException {
		FileOutputStream nfos = null;
		try {
			File file = new File(generateFileName(nowDate));
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			nfos = new FileOutputStream(file, true);
		} catch (IOException ex) {
			super.reportError(null, ex, ErrorManager.GENERIC_FAILURE);
		}

		if (nfos != null) {
			if (fos != null) {
				fos.close();
			}
			fos = nfos;
		}

		super.setOutputStream(fos);
	}

	private synchronized String generateFileName(Date date) {
		return pattern.replaceAll("%d", dateFormat.format(date));
	}

	class RollingCalendar extends GregorianCalendar {

		private static final long serialVersionUID = 1L;

		RollingCalendar() {
			super();
		}

		RollingCalendar(TimeZone timeZone, Locale locale) {
			super(timeZone, locale);
		}

		public Date nextCheckDate(Date now) {
			this.setTime(now);

			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.DATE, 1);

			return getTime();
		}
	}

}

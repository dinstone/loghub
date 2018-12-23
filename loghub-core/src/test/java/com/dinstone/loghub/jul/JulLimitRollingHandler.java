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

package com.dinstone.loghub.jul;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dinstone.loghub.spi.NamedThreadFactory;

/**
 * Simple limit rolling file handler, support limit days files clean.
 *
 * @author dinstone
 */
public class JulLimitRollingHandler extends StreamHandler {

	private static final ExecutorService LOG_FILES_CLEANER = Executors
			.newSingleThreadExecutor(new NamedThreadFactory("LogFilesCleaner", true));

	private static final String DEFAULT_PATTERN = "logs/jul.log";

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private final RollingCalendar calendar = new RollingCalendar();

	/**
	 * current rolling Instant
	 */
	private final Date lastCheck = new Date();

	/**
	 * meter written length for file output stream
	 */
	private MeteredStream meter;

	/**
	 * log file name pattern: [Prefix_]YYYY-DD-MM[_Unique][.Suffix]
	 */
	private Pattern pattern;

	/**
	 * Keep history logs for up to limit days
	 */
	private int limitDays;

	/**
	 * zero => no limit.
	 */
	private int limitSize;

	/**
	 * next rolling Instant
	 */
	private long nextCheck;

	/**
	 * The directory in which log files are created.
	 */
	private String directory;

	/**
	 * The prefix that is added to log file filenames.
	 */
	private String prefix;

	/**
	 * The suffix that is added to log file filenames.
	 */
	private String suffix;

	/**
	 * The unique that is added to log file filenames.
	 */
	private int unique;

	public JulLimitRollingHandler() throws IOException {
		configure();
		activate();
	}

	public JulLimitRollingHandler(String pattern, int limitDays, int limitSize) throws IOException {
		if (pattern == null || pattern.length() < 1) {
			throw new IllegalArgumentException("pattern is empty");
		}

		configure();

		if (limitDays < 0) {
			limitDays = 0;
		}

		this.limitDays = limitDays;
		this.limitSize = limitSize;
		setPattern(pattern);

		activate();
	}

	@Override
	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}

		// check date rolling
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis >= nextCheck) {
			if (limitSize > 0) {
				unique = 0;
			}
			lastCheck.setTime(currentTimeMillis);
			nextCheck = calendar.nextCheckDate(lastCheck).getTime();
			try {
				rollover();
			} catch (IOException ioe) {
				if (ioe instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
			}
		} else if (limitSize > 0 && meter.written >= limitSize) {
			try {
				unique++;
				rollover();
			} catch (IOException ioe) {
				if (ioe instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
			}
		}

		super.publish(record);
		super.flush();
	}

	private void configure() throws IOException {
		String cname = getClass().getName();
		setPattern(getProperty(cname + ".pattern", DEFAULT_PATTERN));
		setLevel(getPropertyLevel(cname + ".level"));
		setFilter(getPropertyFilter(cname + ".filter"));
		setFormatter(getPropertyFormatter(cname + ".formatter"));
		try {
			setEncoding(getProperty(cname + ".encoding", null));
		} catch (Exception ex) {
			try {
				setEncoding(null);
			} catch (Exception ex2) {
			}
		}

		limitDays = getPropertyInteger(cname + ".limitDays", 30);
		limitSize = getPropertyInteger(cname + ".limitSize", 0);

		// Set error manager
		setErrorManager(new ErrorManager());
	}

	private void setPattern(String pattern) throws IOException {
		File logFile = new File(pattern);
		if (logFile.isDirectory()) {
			directory = pattern;
		} else {
			directory = logFile.getCanonicalFile().getParent();
			String fileName = logFile.getName();
			int index = fileName.lastIndexOf(".");
			if (index > -1) {
				prefix = fileName.substring(0, index);
				suffix = fileName.substring(index + 1);
			} else {
				prefix = fileName;
				suffix = "";
			}
		}
	}

	private void activate() throws IOException {
		nextCheck = calendar.nextCheckDate(lastCheck).getTime();

		StringBuilder b = new StringBuilder("^");
		if (prefix != null && !prefix.isEmpty()) {
			b.append(Pattern.quote(prefix));
			b.append("_");
		}
		b.append("(\\d{4}-\\d{1,2}-\\d{1,2})");
		if (limitSize > 0) {
			b.append("_(\\d*)");
		}
		if (suffix != null && !suffix.isEmpty()) {
			b.append("\\.");
			b.append(Pattern.quote(suffix));
		}
		b.append("$");
		pattern = Pattern.compile(b.toString());

		unique = findUnique();

		rollover();
	}

	private int findUnique() {
		File logDir = new File(directory);
		if (!logDir.exists()) {
			logDir.mkdirs();
		}

		if (limitSize <= 0) {
			return -1;
		}

		for (int unique = 0;; unique++) {
			File lf = generateFile(directory, prefix, suffix, lastCheck, unique);
			if (lf.exists() && limitSize > 0 && lf.length() >= limitSize) {
				continue;
			} else {
				return unique;
			}
		}
	}

	private File generateFile(String directory, String prefix, String suffix, Date lastCheck, int unique) {
		StringBuilder b = new StringBuilder();
		if (prefix != null && !prefix.isEmpty()) {
			b.append(prefix).append("_");
		}
		b.append(dateFormat.format(lastCheck));
		if (unique >= 0) {
			b.append("_").append(unique);
		}
		if (suffix != null && !suffix.isEmpty()) {
			b.append(".").append(suffix);
		}
		return new File(directory, b.toString());
	}

	private void rollover() throws IOException {
		rolling();
		try {
			clearing();
		} catch (Exception e) {
			super.reportError(null, e, ErrorManager.GENERIC_FAILURE);
		}
	}

	private void rolling() throws IOException {
		MeteredStream nfos = null;
		try {
			File lastFile = generateFile(directory, prefix, suffix, lastCheck, unique);
			nfos = new MeteredStream(new FileOutputStream(lastFile, true), lastFile.length());
		} catch (IOException ex) {
			super.reportError(null, ex, ErrorManager.GENERIC_FAILURE);
		}

		if (nfos != null) {
			if (meter != null) {
				meter.close();
			}
			meter = nfos;
		}

		super.setOutputStream(meter);
	}

	private void clearing() {
		LOG_FILES_CLEANER.submit(new Runnable() {

			@Override
			public void run() {
				File[] files = getHistoryLogFiles();
				if (files != null && files.length > 0) {
					for (File file : files) {
						file.delete();
					}
				}
			}
		});

	}

	private File[] getHistoryLogFiles() {
		final Date offset = new RollingCalendar().offsetDate(lastCheck, -limitDays);
		return new File(directory).listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				try {
					String fileName = file.getName();
					Matcher matcher = pattern.matcher(fileName);
					if (matcher.matches()) {
						Date history = dateFormat.parse(matcher.group(1));
						return history.before(offset);
					}
					return false;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}

	private String getProperty(String name, String defaultValue) {
		String value = LogManager.getLogManager().getProperty(name);
		if (value == null) {
			value = defaultValue;
		} else {
			value = value.trim();
		}
		return value;
	}

	private int getPropertyInteger(String propKey, int defaultValue) {
		LogManager logManager = LogManager.getLogManager();
		String val = logManager.getProperty(propKey);
		try {
			if (val != null) {
				return Integer.parseInt(val);
			}
		} catch (Exception ex) {
			// We got one of a variety of exceptions in creating the
			// class or creating an instance. Drop through.
		}
		return defaultValue;
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
			// class or creating an instance. Drop through.
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
			// class or creating an instance. Drop through.
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

	/**
	 * A metered stream is a subclass of OutputStream that (a) forwards all its
	 * output to a target stream (b) keeps track of how many bytes have been written
	 */
	private class MeteredStream extends OutputStream {

		final OutputStream out;

		long written;

		MeteredStream(OutputStream out, long written) {
			this.out = new BufferedOutputStream(out);
			this.written = written;
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
			written++;
		}

		@Override
		public void write(byte buff[]) throws IOException {
			out.write(buff);
			written += buff.length;
		}

		@Override
		public void write(byte buff[], int off, int len) throws IOException {
			out.write(buff, off, len);
			written += len;
		}

		@Override
		public void flush() throws IOException {
			out.flush();
		}

		@Override
		public void close() throws IOException {
			out.close();
		}
	}

	private class RollingCalendar extends GregorianCalendar {

		private static final long serialVersionUID = 1L;

		RollingCalendar() {
			super();
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

		public Date offsetDate(Date now, int offset) {
			this.setTime(now);

			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.DATE, offset);

			return getTime();
		}
	}

}

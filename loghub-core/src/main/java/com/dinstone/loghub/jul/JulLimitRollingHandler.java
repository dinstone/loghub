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

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
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
 * Simple limit rolling file handler, support max file clear.
 *
 * @author dinstone
 */
public class JulLimitRollingHandler extends StreamHandler {

	private static final String DEFAULT_LOG_FILE_PATTERN = "logs/jul_%d.log";

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private final RollingCalendar calendar = new RollingCalendar();

	private final Date nowDate = new Date();

	private MeteredStream meter;

	private String nowLogFile;

	private int maxFileSize;

	private String pattern;

	private int limit; // zero => no limit.

	public JulLimitRollingHandler() throws IOException {
		configure();
		activate();
	}

	public JulLimitRollingHandler(String pattern, int maxFileSize) throws IOException {
		if (pattern == null || pattern.length() < 1) {
			throw new IllegalArgumentException("pattern is empty");
		}
		if (maxFileSize <= 0) {
			maxFileSize = 1;
		}

		configure();

		this.pattern = pattern;
		this.maxFileSize = maxFileSize;

		activate();
	}

	@Override
	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}

		if (limit > 0 && meter.written >= limit) {
			try {
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

	/**
	 * A metered stream is a subclass of OutputStream that (a) forwards all its
	 * output to a target stream (b) keeps track of how many bytes have been written
	 */
	private class MeteredStream extends OutputStream {
		final OutputStream out;
		int written;

		MeteredStream(OutputStream out, int written) {
			this.out = out;
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

		maxFileSize = getPropertyMaxFileSize(cname + ".maxFileSize");
	}

	private void activate() throws IOException {
		if (!pattern.contains("%d")) {
			pattern += "%d";
		}

//		nextCheck = calendar.nextCheckDate(nowDate).getTime();

		rollover();
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
		FileOutputStream nfos = null;
		try {
			nowLogFile = generateLogFile(nowDate);
			File file = new File(nowLogFile);
			if (!file.exists() && file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			nfos = new FileOutputStream(file, true);
		} catch (IOException ex) {
			super.reportError(null, ex, ErrorManager.GENERIC_FAILURE);
		}

//		if (nfos != null) {
//			if (fos != null) {
//				fos.close();
//			}
//			fos = nfos;
//		}
//
//		super.setOutputStream(fos);
	}

	private void clearing() {
		File[] files = getHistoryLogFiles();
		if (files != null && files.length > 0) {
			Arrays.sort(files, new Comparator<File>() {

				@Override
				public int compare(File f1, File f2) {
					return (int) (f1.lastModified() - f2.lastModified());
				}
			});

			if (files.length >= maxFileSize) {
				for (int i = 0; i <= files.length - maxFileSize; i++) {
					files[i].delete();
				}
			}
		}
	}

	private int getPropertyMaxFileSize(String maxFileSizeKey) {
		LogManager logManager = LogManager.getLogManager();
		String val = logManager.getProperty(maxFileSizeKey);
		try {
			if (val != null) {
				int v = Integer.parseInt(val);
				if (v <= 0) {
					v = 1;
				}
				return v;
			}
		} catch (Exception ex) {
			// We got one of a variety of exceptions in creating the
			// class or creating an instance. Drop through.
		}
		return Integer.MAX_VALUE;
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

	private File[] getHistoryLogFiles() {
		final File file = new File(pattern);
		File logPath = file.getParentFile();
		if (logPath == null) {
			logPath = new File(".");
		}

		final File nfile = new File(nowLogFile);
		return logPath.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				try {
					if (nfile.getName().equals(file.getName())) {
						return false;
					}
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}

	private synchronized String generateLogFile(Date date) {
		if (limit > 0) {
			
		}
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

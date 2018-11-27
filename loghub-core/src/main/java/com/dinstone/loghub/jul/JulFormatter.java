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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * JUL log record formatter.<br>
 * yyyy-MM-dd HH:mm:ss.SSS [level] Class.Method - message
 * 
 * @author dinstone
 *
 */
public class JulFormatter extends Formatter {

	private static final String PLACE_HOLDER_REGEX = "\\{\\}";

	private static final String PLACE_HOLDER_CHARS = "{}";

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(dateFormat.format(new Date(record.getMillis())));
		sb.append(" [");
		sb.append(record.getLevel());
		sb.append("] ");

		// source
		if (record.getSourceClassName() != null) {
			sb.append(record.getSourceClassName());
			if (record.getSourceMethodName() != null) {
				sb.append(".").append(record.getSourceMethodName());
			}
		} else {
			sb.append(record.getLoggerName());
		}
		sb.append(" - ");

		String message = record.getMessage() == null ? "" : record.getMessage();
		message = resolvePlaceHolder(message, record.getParameters());
		// message = String.format(message, record.getParameters());

		sb.append(message);
		sb.append(" ");

		Throwable throwable = record.getThrown();
		if (throwable == null) {
			sb.append("\n");
			return sb.toString();
		}
		StackTraceElement[] stackElements = throwable.getStackTrace();
		if (stackElements == null) {
			sb.append("\n");
			return sb.toString();
		}
		sb.append(throwable.getMessage());
		sb.append("\n");
		for (StackTraceElement ele : stackElements) {
			sb.append("  at");
			sb.append(" ");
			sb.append(ele.getClassName());
			sb.append(".");
			sb.append(ele.getMethodName());
			sb.append("(");
			sb.append(ele.getFileName());
			sb.append(":");
			sb.append(ele.getLineNumber());
			sb.append(")");
			sb.append("\n");
		}
		return sb.toString();
	}

	public String resolvePlaceHolder(String msg, Object[] parameters) throws IllegalArgumentException {
		if (parameters == null || parameters.length == 0) {
			return msg;
		}
		int parameterSize = parameters.length;
		int fromIndex = 0;
		int count = 0;
		while (fromIndex >= 0 && count < parameterSize) {
			fromIndex = msg.indexOf(PLACE_HOLDER_CHARS, fromIndex + 1);
			if (fromIndex >= 0 && count < parameterSize) {
				msg = msg.replaceFirst(PLACE_HOLDER_REGEX, String.valueOf(parameters[count]));
			}
			count++;
		}
		return msg;
	}
}

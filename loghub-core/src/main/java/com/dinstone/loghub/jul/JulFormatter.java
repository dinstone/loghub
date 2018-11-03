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

public class JulFormatter extends Formatter {

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
		sb.append(" ");

		String message = record.getMessage() == null ? "" : record.getMessage();
		message = resolvePlaceHolder(message, record.getParameters(), "{}");
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

	public String resolvePlaceHolder(String msg, Object[] parameters, String placeHolder)
			throws IllegalArgumentException {
		if (parameters == null)
			return msg;
		int parameterSize = parameters.length;
		int fromIndex = 0;
		int i = 0;
		do {
			fromIndex = msg.indexOf(placeHolder, fromIndex + 1);
			if (fromIndex >= 0 && i < parameterSize) {
				msg = msg.replace(placeHolder, String.valueOf(parameters[i]));
			}
			i++;
		} while (fromIndex >= 0 && i < parameterSize);
		return msg;
	}
}

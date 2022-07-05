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

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * simple console handler
 * 
 * @author dinstone
 *
 */
public class JulConsoleHandler extends StreamHandler {

	public JulConsoleHandler(OutputStream out, Formatter formatter) {
		super(out, formatter);
	}

	@Override
	public synchronized void publish(LogRecord record) {
		super.publish(record);

		flush();
	}

	@Override
	public synchronized void close() throws SecurityException {
		flush();
	}
}

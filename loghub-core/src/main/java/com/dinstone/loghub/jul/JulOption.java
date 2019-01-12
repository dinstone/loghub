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
package com.dinstone.loghub.jul;

import java.util.logging.Formatter;
import java.util.logging.Level;

public class JulOption {

	private Formatter formatter = new JulFormatter();

	private Level level = Level.INFO;

	@Deprecated
	private int maxFileSize = 31;

	/**
	 * Keep history logs for up to limit days
	 */
	private int limitDays = 30;

	/**
	 * Log file size : zero => no limit.
	 */
	private int limitSize;

	private String pattern;

	private boolean console;

	public String getPattern() {
		return pattern;
	}

	public JulOption setPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}

	public Level getLevel() {
		return level;
	}

	public JulOption setLevel(Level level) {
		this.level = level;
		return this;
	}

	public Formatter getFormatter() {
		return formatter;
	}

	public JulOption setFormatter(Formatter formatter) {
		this.formatter = formatter;
		return this;
	}

	public boolean isConsole() {
		return console;
	}

	public JulOption setConsole(boolean console) {
		this.console = console;
		return this;
	}

	@Deprecated
	public int getMaxFileSize() {
		return maxFileSize;
	}

	@Deprecated
	public JulOption setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
		return this;
	}

	public int getLimitDays() {
		return limitDays;
	}

	public JulOption setLimitDays(int limitDays) {
		this.limitDays = limitDays;
		return this;
	}

	public int getLimitSize() {
		return limitSize;
	}

	public JulOption setLimitSize(int limitSize) {
		this.limitSize = limitSize;
		return this;
	}

}

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

import java.util.logging.Formatter;
import java.util.logging.Level;

public class JulOption {

	private Formatter formatter = new JulFormatter();

	private Level level = Level.INFO;

	private int maxFileSize = 31;

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

	public int getMaxFileSize() {
		return maxFileSize;
	}

	public JulOption setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
		return this;
	}

}

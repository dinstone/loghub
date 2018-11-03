
package com.dinstone.loghub.jul;

import java.util.logging.Formatter;
import java.util.logging.Level;

public class JulOption {

	private String pattern;

	private boolean console;

	private Level level = Level.INFO;

	private Formatter formatter = new JulFormatter();

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

}

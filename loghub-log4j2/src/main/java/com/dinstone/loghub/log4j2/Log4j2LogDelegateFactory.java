package com.dinstone.loghub.log4j2;

import com.dinstone.loghub.spi.LogDelegate;
import com.dinstone.loghub.spi.LogDelegateFactory;

/**
 * @author Clement Escoffier - clement@apache.org
 * @author dinstone
 */
public class Log4j2LogDelegateFactory implements LogDelegateFactory {

	public LogDelegate createDelegate(final String name) {
		return new Log4j2LogDelegate(name);
	}

}

package com.dinstone.loghub.log4j;

import com.dinstone.loghub.spi.LogDelegate;
import com.dinstone.loghub.spi.LogDelegateFactory;

/**
 * @author <a href="kenny.macleod@kizoom.com">Kenny MacLeod</a>
 * @author dinstone
 */
public class Log4jLogDelegateFactory implements LogDelegateFactory {

	public LogDelegate createDelegate(final String name) {
		return new Log4jLogDelegate(name);
	}

}

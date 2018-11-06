package com.dinstone.loghub.slf4j;

import com.dinstone.loghub.spi.LogDelegate;
import com.dinstone.loghub.spi.LogDelegateFactory;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author dinstone
 */
public class Slf4jLogDelegateFactory implements LogDelegateFactory {

	public LogDelegate createDelegate(final String clazz) {
		return new Slf4jLogDelegate(clazz);
	}

}

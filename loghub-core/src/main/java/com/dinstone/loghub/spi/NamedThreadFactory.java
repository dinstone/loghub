package com.dinstone.loghub.spi;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

	private final AtomicInteger threadNumber = new AtomicInteger(1);

	private final ThreadGroup group;

	private final String prefix;

	private final boolean daemon;

	public NamedThreadFactory(String name, boolean daemon) {
		final SecurityManager s = System.getSecurityManager();
		this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.prefix = name + "-";
		this.daemon = daemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		final Thread t = new Thread(group, r, prefix + threadNumber.getAndIncrement(), 0);
		t.setDaemon(daemon);
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}

}

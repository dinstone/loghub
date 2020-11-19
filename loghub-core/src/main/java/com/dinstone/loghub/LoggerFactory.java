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
package com.dinstone.loghub;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.dinstone.loghub.jul.JulDelegateFactory;
import com.dinstone.loghub.spi.LogDelegateFactory;

public class LoggerFactory {

    public static final String LOGGER_DELEGATE_FACTORY_CLASS = "logger.delegate.factory.class";

    private static final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();

    private static volatile LogDelegateFactory delegateFactory;

    static {
        initialise();
    }

    private static synchronized void initialise() {
        LogDelegateFactory delegateFactory = null;
        try {
            delegateFactory = configureWith(System.getProperty(LOGGER_DELEGATE_FACTORY_CLASS));
        } catch (Exception e) {
        }
        if (delegateFactory == null) {
            delegateFactory = configureWith("com.dinstone.loghub.slf4j.Slf4jLogDelegateFactory");
        }
        if (delegateFactory == null) {
            delegateFactory = configureWith("com.dinstone.loghub.log4j2.Log4j2LogDelegateFactory");
        }
        if (delegateFactory == null) {
            delegateFactory = configureWith("com.dinstone.loghub.log4j.Log4jLogDelegateFactory");
        }
        if (delegateFactory == null) {
            delegateFactory = new JulDelegateFactory();
        }

        LoggerFactory.delegateFactory = delegateFactory;
    }

    @SuppressWarnings("deprecation")
    private static LogDelegateFactory configureWith(String name) {
        String loggerName = LoggerFactory.class.getName();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = Class.forName(name, true, loader);
            LogDelegateFactory factory = (LogDelegateFactory) clazz.newInstance();
            factory.createDelegate(loggerName).debug("Using " + clazz.getName());
            return factory;
        } catch (Throwable ignore) {
        }
        return null;
    }

    public static synchronized void initialise(LogDelegateFactory delegateFactory) {
        LoggerFactory.delegateFactory = delegateFactory;
    }

    public static Logger getLogger(final Class<?> clazz) {
        String name = clazz.isAnonymousClass() ? clazz.getEnclosingClass().getCanonicalName()
                : clazz.getCanonicalName();
        return getLogger(name);
    }

    public static Logger getLogger(final String name) {
        Logger logger = loggers.get(name);
        if (logger == null) {
            logger = new Logger(delegateFactory.createDelegate(name));
            Logger oldLogger = loggers.putIfAbsent(name, logger);
            if (oldLogger != null) {
                logger = oldLogger;
            }
        }

        return logger;
    }

    public static void removeLogger(String name) {
        loggers.remove(name);
    }
}

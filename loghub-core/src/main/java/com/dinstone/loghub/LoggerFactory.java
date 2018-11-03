
package com.dinstone.loghub;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.dinstone.loghub.jul.JulDelegateFactory;
import com.dinstone.loghub.spi.LogDelegate;
import com.dinstone.loghub.spi.LogDelegateFactory;

public class LoggerFactory {

    public static final String LOGGER_DELEGATE_FACTORY_CLASS = "logger.delegate.factory.class";

    private static final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();

    private static volatile LogDelegateFactory delegateFactory;

    static {
        initialise();
    }

    private static synchronized void initialise() {
        LogDelegateFactory delegateFactory;

        // If a system property is specified then this overrides any delegate factory which is set
        // programmatically - this is primarily of use so we can configure the logger delegate on the client side.
        // call to System.getProperty is wrapped in a try block as it will fail if the client runs in a secured
        // environment
        String className = JulDelegateFactory.class.getName();
        try {
            className = System.getProperty(LOGGER_DELEGATE_FACTORY_CLASS);
        } catch (Exception e) {
        }

        if (className != null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                Class<?> clz = loader.loadClass(className);
                delegateFactory = (LogDelegateFactory) clz.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Error instantiating transformer class \"" + className + "\"", e);
            }
        } else {
            delegateFactory = new JulDelegateFactory();
        }

        LoggerFactory.delegateFactory = delegateFactory;
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
            LogDelegate delegate = delegateFactory.createDelegate(name);
            logger = new Logger(delegate);
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

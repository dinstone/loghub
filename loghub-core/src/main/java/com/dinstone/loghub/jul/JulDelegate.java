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

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.dinstone.loghub.spi.LogDelegate;

public class JulDelegate implements LogDelegate {

    protected final java.util.logging.Logger logger;

    JulDelegate(final String name) {
        logger = java.util.logging.Logger.getLogger(name);
    }

    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    public void fatal(final Object message) {
        log(Level.SEVERE, message);
    }

    public void fatal(final Object message, final Throwable t) {
        log(Level.SEVERE, message, t);
    }

    public void error(final Object message) {
        log(Level.SEVERE, message);
    }

    @Override
    public void error(Object message, Object... params) {
        log(Level.SEVERE, message, null, params);
    }

    public void error(final Object message, final Throwable t) {
        log(Level.SEVERE, message, t);
    }

    @Override
    public void error(Object message, Throwable t, Object... params) {
        log(Level.SEVERE, message, t, params);
    }

    public void warn(final Object message) {
        log(Level.WARNING, message);
    }

    @Override
    public void warn(Object message, Object... params) {
        log(Level.WARNING, message, null, params);
    }

    public void warn(final Object message, final Throwable t) {
        log(Level.WARNING, message, t);
    }

    @Override
    public void warn(Object message, Throwable t, Object... params) {
        log(Level.WARNING, message, t, params);
    }

    public void info(final Object message) {
        log(Level.INFO, message);
    }

    @Override
    public void info(Object message, Object... params) {
        log(Level.INFO, message, null, params);
    }

    public void info(final Object message, final Throwable t) {
        log(Level.INFO, message, t);
    }

    @Override
    public void info(Object message, Throwable t, Object... params) {
        log(Level.INFO, message, t, params);
    }

    public void debug(final Object message) {
        log(Level.FINE, message);
    }

    @Override
    public void debug(Object message, Object... params) {
        log(Level.FINE, message, null, params);
    }

    public void debug(final Object message, final Throwable t) {
        log(Level.FINE, message, t);
    }

    @Override
    public void debug(Object message, Throwable t, Object... params) {
        log(Level.FINE, message, t, params);
    }

    public void trace(final Object message) {
        log(Level.FINEST, message);
    }

    @Override
    public void trace(Object message, Object... params) {
        log(Level.FINEST, message, null, params);
    }

    public void trace(final Object message, final Throwable t) {
        log(Level.FINEST, message, t);
    }

    @Override
    public void trace(Object message, Throwable t, Object... params) {
        log(Level.FINEST, message, t, params);
    }

    private void log(Level level, Object message) {
        log(level, message, null);
    }

    private void log(Level level, Object message, Throwable t) {
        log(level, message, t, (Object[]) null);
    }

    protected void log(Level level, Object message, Throwable t, Object... params) {
        if (!logger.isLoggable(level)) {
            return;
        }
        logger.log(createLogRecord(level, message, t, params));
    }

    protected LogRecord createLogRecord(Level level, Object message, Throwable t, Object... params) {
        String msg = (message == null) ? "NULL" : message.toString();
        LogRecord record = new LogRecord(level, msg);
        record.setLoggerName(logger.getName());
        if (t != null) {
            record.setThrown(t);
        } else if (params != null && params.length != 0 && params[params.length - 1] instanceof Throwable) {
            // The exception may be the last parameters (SLF4J uses this convention).
            record.setThrown((Throwable) params[params.length - 1]);
        }
        // This will disable stack trace lookup inside JUL. If someone wants location info, they can use their own
        // formatter
        // or use a different logging framework like sl4j, or log4j
        record.setSourceClassName(null);
        record.setParameters(params);
        return record;
    }

}

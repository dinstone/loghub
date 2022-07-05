/*
 * Copyright (C) 2018-2022 dinstone<dinstone@163.com>
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

import com.dinstone.loghub.spi.LogDelegate;

public class Logger {

    final LogDelegate delegate;

    public Logger(final LogDelegate delegate) {
        this.delegate = delegate;
    }

    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    public void fatal(final Object message) {
        delegate.fatal(message);
    }

    public void fatal(final Object message, final Throwable t) {
        delegate.fatal(message, t);
    }

    public void error(final Object message) {
        delegate.error(message);
    }

    public void error(final Object message, final Throwable t) {
        delegate.error(message, t);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void error(final Object message, final Object... objects) {
        delegate.error(message, objects);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void error(final Object message, final Throwable t, final Object... objects) {
        delegate.error(message, t, objects);
    }

    public void warn(final Object message) {
        delegate.warn(message);
    }

    public void warn(final Object message, final Throwable t) {
        delegate.warn(message, t);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void warn(final Object message, final Object... objects) {
        delegate.warn(message, objects);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void warn(final Object message, final Throwable t, final Object... objects) {
        delegate.warn(message, t, objects);
    }

    public void info(final Object message) {
        delegate.info(message);
    }

    public void info(final Object message, final Throwable t) {
        delegate.info(message, t);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void info(final Object message, final Object... objects) {
        delegate.info(message, objects);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void info(final Object message, final Throwable t, final Object... objects) {
        delegate.info(message, t, objects);
    }

    public void debug(final Object message) {
        delegate.debug(message);
    }

    public void debug(final Object message, final Throwable t) {
        delegate.debug(message, t);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void debug(final Object message, final Object... objects) {
        delegate.debug(message, objects);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void debug(final Object message, final Throwable t, final Object... objects) {
        delegate.debug(message, t, objects);
    }

    public void trace(final Object message) {
        delegate.trace(message);
    }

    public void trace(final Object message, final Throwable t) {
        delegate.trace(message, t);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void trace(final Object message, final Object... objects) {
        delegate.trace(message, objects);
    }

    /**
     * @throws UnsupportedOperationException
     *         if the logging backend does not support parameterized messages
     */
    public void trace(final Object message, final Throwable t, final Object... objects) {
        delegate.trace(message, t, objects);
    }

    /**
     * @return the delegate instance sending operations to the underlying logging framework
     */
    public LogDelegate getDelegate() {
        return delegate;
    }
}
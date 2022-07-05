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
package com.dinstone.loghub.slf4j;

import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

import com.dinstone.loghub.spi.LogDelegate;
import com.dinstone.loghub.spi.LogDelegateFactory;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author dinstone
 */
public class Slf4jLogDelegateFactory implements LogDelegateFactory {

    public Slf4jLogDelegateFactory() {
        if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
            throw new NoClassDefFoundError("NOPLoggerFactory not supported");
        }
    }

    @Override
    public LogDelegate createDelegate(final String clazz) {
        return new Slf4jLogDelegate(clazz);
    }

}

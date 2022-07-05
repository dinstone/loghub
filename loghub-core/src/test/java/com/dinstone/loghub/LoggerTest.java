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

import org.junit.Test;

import com.dinstone.loghub.jul.JulDelegateFactory;
import com.dinstone.loghub.jul.JulOption;

public class LoggerTest {

    @Test
    public void test00() throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(LoggerTest.class);
        for (int i = 0; i < 5; i++) {
            logger.error("retry register service {}",
                    "ServiceDescription [app=com.rpc.demo.server, code=com.rpc.demo.server@172.29.151.185:3333$, name=com.dinstone.focus.example.DemoService, group=, host=172.29.151.185, port=3333, uri=null, attributes={methods=[java.lang.String com.dinstone.focus.example.DemoService.hello(java.lang.String)], timeout=3000}]");
            logger.warn("warn {}", i);
            logger.info("info {}", i);
            logger.debug("debug {}", i);
            logger.trace("trace {}", i);
        }

        Thread.sleep(1000);
    }

    @Test
    public void test01() throws InterruptedException {
        LoggerFactory.getLogger("");

        JulOption option = new JulOption().setPattern("logs/loghub.log").setLimitDays(3);
        JulDelegateFactory factory = new JulDelegateFactory(option);
        LoggerFactory.initialise(factory);

        Logger logger = LoggerFactory.getLogger(LoggerTest.class);
        for (int i = 0; i < 5; i++) {
            logger.error("error {}", i);
            logger.warn("warn {}", i);
            logger.info("info {}", i);
            logger.debug("debug {}", i);
            logger.trace("trace {}", i);
        }

        Thread.sleep(1000);
    }

    @Test
    public void test02() throws InterruptedException {
        LoggerFactory.getLogger("");

        JulOption option = new JulOption().setConsole(true);
        JulDelegateFactory factory = new JulDelegateFactory(option);
        LoggerFactory.initialise(factory);

        Logger logger = LoggerFactory.getLogger(LoggerTest.class);
        for (int i = 0; i < 5; i++) {
            logger.error("{} error {}", "{}-" + (i + 1), i);
            logger.error("error {}", i);
            logger.warn("warn {}", i);
            logger.info("info {}", i);
            logger.debug("debug {}", i);
            logger.trace("trace {}", i);
        }

        Thread.sleep(1000);
    }

}

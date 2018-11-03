
package com.dinstone.loghub;

import org.junit.Test;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.loghub.jul.JulDelegateFactory;
import com.dinstone.loghub.jul.JulOption;

public class LoggerTest {

    @Test
    public void test() throws InterruptedException {
        JulOption option = new JulOption().setPattern("logs/jul_%d.log");
        JulDelegateFactory factory = new JulDelegateFactory(option);
        LoggerFactory.initialise(factory);

        Logger logger = LoggerFactory.getLogger(LoggerTest.class);
        for (int i = 0; i < 1000; i++) {
            logger.info("asdfasdfasdf {},{}", i);
        }

        Thread.sleep(3000);

    }

}

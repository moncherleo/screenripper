package com.screenripper.tests.webdriver;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import org.junit.Test;

public class SimpleTest extends BaseTest {
    @Test
    public void mostSimplestTest() {
        driver.get("https://google.com");
        threadSleep(10000);
        driver.close();
        driver = new BaseTest().getDriver();
        driver.get("https://facebook.com");
        threadSleep(10000);
        driver.quit();

    }

    public void threadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

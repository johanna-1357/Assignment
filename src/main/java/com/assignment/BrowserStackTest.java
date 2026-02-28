package com.assignment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.URL;

public class BrowserStackTest {

    public static final String USERNAME = "YOUR_USERNAME";
    public static final String ACCESS_KEY = "YOUR_ACCESS_KEY";

    @Test(threadPoolSize = 5, invocationCount = 5)
    public void runParallelTest() throws Exception {

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("browserVersion", "latest");
        caps.setCapability("os", "Windows");
        caps.setCapability("osVersion", "11");
        caps.setCapability("name", "ElPais Parallel Test");

        WebDriver driver = new RemoteWebDriver(
                new URL("https://" + USERNAME + ":" + ACCESS_KEY +
                        "@hub-cloud.browserstack.com/wd/hub"), caps
        );

        driver.get("https://elpais.com/");
        Thread.sleep(5000);
        driver.quit();
    }
}
package com.assignment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.HashMap;

public class BrowserStackTest {
    public static final String USERNAME = "BROWSERSTACK_USERNAME";
    public static final String ACCESS_KEY = "BROWSERSTACK_ACCESS_KEY";
    @Test(threadPoolSize = 1, invocationCount = 1)
    public void runParallelTest() throws Exception {

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("browserVersion", "latest");

        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "11");
        bstackOptions.put("buildName", "ElPais Automation Build");
        bstackOptions.put("sessionName", "ElPais Test Session");

        caps.setCapability("bstack:options", bstackOptions);

        WebDriver driver = new RemoteWebDriver(
                new URL("https://" + USERNAME + ":" + ACCESS_KEY +
                        "@hub-cloud.browserstack.com/wd/hub"),
                caps
        );

        driver.get("https://elpais.com/");
        Thread.sleep(5000);
        driver.quit();
    }
}
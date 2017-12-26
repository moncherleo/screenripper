package com.screenripper.tests.webdriver;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class BaseTest {
    protected WebDriver driver;
    private static String BROWSER = System.getProperty("browser");
    private static String REMOTE = System.getProperty("remote");
    private static String REMOTE_URL = "http://localhost:4444/wd/hub";
    private static String OS = System.getProperty("os.name").toLowerCase();
    DesiredCapabilities capabilities = new DesiredCapabilities();

    private static final String CHROME_PATH_MAC = "src/test/resources/drivers/chromedriver";
    private static final String CHROME_PATH_WIN = "src/test/resources/drivers/chromedriver.exe";

    @Rule
    public TestWatcher screenshotOnFail = new TestWatcher() {

        @Override
        protected void failed(Throwable e, Description description) {
            makeScreenshotOnFailure("Screenshot on failure");
        }

        @Override
        protected void finished(Description description) {
            driver.quit();
        }
    };

    @Attachment(value = "{0}", type = "image/png")
    public byte[] makeScreenshotOnFailure(String attachName) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Before
    public void setUp() {

        //mvn clean test -Dbrowser="Chrome"

        if (BROWSER == null || BROWSER.equalsIgnoreCase("Firefox") || BROWSER.equalsIgnoreCase("")) {
            this.driver = new FirefoxDriver();
            capabilities.setBrowserName("firefox");
        } else if (BROWSER.equalsIgnoreCase("Chrome")) {
            if (isWindows()) {
                System.setProperty("webdriver.chrome.driver", CHROME_PATH_WIN);
            } else if (isMac()) {
                System.setProperty("webdriver.chrome.driver", CHROME_PATH_MAC);
            }
            capabilities.setBrowserName("chrome");
            this.driver = new ChromeDriver();
        }

        if (REMOTE != null && REMOTE.equalsIgnoreCase("true")) {
            try {
                this.driver.quit();
                this.driver = new RemoteWebDriver(new URL(REMOTE_URL), capabilities);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();

    }

    @After
    public void tearDown() {
        //moved to TestWatcher
        //driver.close();
    }

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

}

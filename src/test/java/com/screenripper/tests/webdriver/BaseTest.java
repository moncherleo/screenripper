package com.screenripper.tests.webdriver;

import com.screenripper.pages.MyAccountPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BaseTest {

    org.slf4j.Logger log = LoggerFactory.getLogger(BaseTest.class);

    public WebDriver driver;
    private static String BROWSER = System.getProperty("browser");
    //    private static String BROWSER = "chrome";
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
            try{
                makeScreenshotOnFailure("Screenshot on failure");
                logout();
            } catch (Exception ex){
                ex.printStackTrace();
            } finally {
                quitDriver();
            }
        }

        @Override
        protected void finished(Description description) {
            logout();
            quitDriver();
        }

        @Override
        protected void skipped (org.junit.AssumptionViolatedException e, Description description){
            quitDriver();
        }
    };

    public void logout(){
        if (driver != null) {
            new MyAccountPage(driver).logoutFromAccount();
        }
    }

    public void quitDriver(){
        if (driver != null) {
            driver.quit();
            log.info("The browser is closed");
        }
    }


    @Attachment(value = "{0}", type = "image/png")
    public byte[] makeScreenshotOnFailure(String attachName) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Before
    public void setUp() {
        getDriver();
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

    public WebDriver getDriver() {

        //mvn clean test -Dbrowser="Chrome"

        if (driver == null) {

            if (BROWSER == null || BROWSER.equalsIgnoreCase("Chrome") || BROWSER.equalsIgnoreCase("")) {

                if (isWindows()) {
                    System.setProperty("webdriver.chrome.driver", CHROME_PATH_WIN);
                } else if (isMac()) {
                    System.setProperty("webdriver.chrome.driver", CHROME_PATH_MAC);
                }
                capabilities.setBrowserName("chrome");

                ChromeOptions chromeOptions = new ChromeOptions();
                Map<String, Object> prefs = new HashMap<String, Object>();
                prefs.put("profile.default_content_setting_values.plugins", 1);
                prefs.put("profile.content_settings.plugin_whitelist.adobe-flash-player", 1);
                prefs.put("profile.content_settings.exceptions.plugins.*,*.per_resource.adobe-flash-player", 1);
                // Enable Flash for this site
                prefs.put("PluginsAllowedForUrls", "https://my.stratoplan.net/");
                chromeOptions.setExperimentalOption("prefs", prefs);

                this.driver = new ChromeDriver(chromeOptions);

            } else if (BROWSER.equalsIgnoreCase("Safari")) {
                capabilities.setBrowserName("safari");
                this.driver = new SafariDriver();

            } else if (BROWSER.equalsIgnoreCase("Firefox")) {

                System.setProperty("webdriver.gecko.driver", "src/test/resources/drivers/geckodriver");
                capabilities = DesiredCapabilities.firefox();
                capabilities.setBrowserName("firefox");
                capabilities.setCapability("marionette", true);

                FirefoxProfile ffProfile = new FirefoxProfile();
                ffProfile.setPreference("dom.ipc.plugins.enabled.libflashplayer.so", "true");
                ffProfile.setPreference("plugin.state.flash", 2);
                capabilities.setCapability(FirefoxDriver.PROFILE, ffProfile);

                FirefoxOptions ffoptions = new FirefoxOptions();
                ffoptions.addPreference("dom.ipc.plugins.enabled.libflashplayer.so", "true");
                ffoptions.addPreference("plugin.state.flash", 2);
                capabilities.setCapability("moz:firefoxOptions", ffoptions);

                this.driver = new FirefoxDriver(capabilities);
            }

            if (REMOTE != null && REMOTE.equalsIgnoreCase("true")) {
                try {
                    this.driver.quit();
                    this.driver = new RemoteWebDriver(new URL(REMOTE_URL), capabilities);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.manage().window().maximize();

            return driver;

        } else return this.driver;
    }
}

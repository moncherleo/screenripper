package com.screenripper.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.allure.annotations.Step;

public class LoginPage extends BasePage {

    Logger log = LoggerFactory.getLogger(LoginPage.class);

    public static final By loginInput = By.cssSelector("#user_login");
    public static final By passwordInput = By.cssSelector("#user_pass");
    public static final By loginButton = By.cssSelector(".et_pb_newsletter_button.et_pb_button");
    //public static final By exitMenuItem = By.xpath("//ul[@id='et-secondary-nav']//li/a[contains(text(),'Выйти')]");
    public static final By exitMenuItem = By.xpath("//a[text()='Выйти']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Navigate to the login page at {0} URL.")
    public LoginPage navigateToLoginPage(String loginPageURL) {
        driver.get(loginPageURL);
        Assert.assertTrue(driver.findElements(loginButton).size() > 0);

        log.info("Login page is opened");

        return this;
    }

    @Step("Login to the MyAccount with username: \"{0}\" and password: \"{1}\".")
    public MyAccountPage loginAs(String username, String password) {

        findElement(loginInput).sendKeys(username);
        findElement(passwordInput).sendKeys(password);
        findElement(loginButton).click();

        //Assert.assertTrue(findElements(loginButton).size() == 0);

        log.info("Successfully logged in as " + username);

        return new MyAccountPage(driver);
    }


    public LoginPage closeAutomationChromeAlert() {

        if (driver instanceof ChromeDriver){

            try {
                Screen screen = new Screen();
                ImagePath.add(imagePath);
                screen.wait("automation_alert_close.png");
                screen.click("automation_alert_close.png");
            } catch (FindFailed findFailed) {
                findFailed.printStackTrace();
            }

        }
        return this;
    }
}

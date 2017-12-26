package com.screenripper.tests.webdriver;

import com.automation.remarks.video.annotations.Video;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RipTheCurlTest extends BaseTest {
    private String loginPageURL = "https://my.stratoplan.net/";
    private String username = "qasmanagers@gmail.com";
    private String password = "y7dwRQuM*j$P95%WM)X!8DMq";
    private String pathToURLsFile = "";
    private List<String> URLs = new ArrayList<>();

    @Test
    @Video
    public void recordTheVideo() {
        readAllURLs(pathToURLsFile);
        navigateToLoginPage();
        loginAs(username, password);
        for (String lessonPageUrl : URLs) {
            navigateToTheLessonPage(lessonPageUrl);
            startVideoPlayback();
            waitTillVideoFinish();
            refreshThePage();
            if (!areWeLoggedIn()) {
                loginAs(username, password);
            }
        }
    }

    private boolean areWeLoggedIn() {
        return false;
    }

    private void refreshThePage() {
    }

    private void waitTillVideoFinish() {
    }

    private void startVideoPlayback() {
        By videoPlayButton = By.cssSelector("div.fp-player");
        By videoFullscreen = By.cssSelector("div.fp-player > div.fp-ui > a.fp-fullscreen");
        driver.findElement(videoPlayButton).click();
        driver.findElement(videoFullscreen).click();
    }

    private void readAllURLs(String pathToURLsFile) {
        Scanner s = null;
        try {
            s = new Scanner(new File(pathToURLsFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (s.hasNext()){
            URLs.add(s.next());
        }
        s.close();
    }

    private void loginAs(String username, String password) {
        By loginInput = By.cssSelector("#user_login");
        By passwordInput = By.cssSelector("#user_pass");
        By loginButton = By.cssSelector(".et_pb_newsletter_button.et_pb_button");
        By exitMenuItem = By.xpath(".//li/a[contains(text(),'Выйти')]");
        findElement(loginInput).sendKeys(username);
        findElement(passwordInput).sendKeys(password);
        findElement(loginButton).click();
        Assert.assertTrue(driver.findElements(exitMenuItem).size() > 0);
    }

    private void navigateToTheLessonPage(String lessonPageUrl) {
        driver.navigate().to(lessonPageUrl);
    }

    private void navigateToLoginPage() {
        driver.get(loginPageURL);
    }

    private WebElement findElement(By by){
        return driver.findElement(by);
    }
}

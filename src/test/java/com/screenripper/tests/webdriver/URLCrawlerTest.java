package com.screenripper.tests.webdriver;

import com.screenripper.pages.LoginPage;
import com.screenripper.pages.MyAccountPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;


import static com.screenripper.tests.webdriver.Config.*;

public class URLCrawlerTest extends BaseTest {
    LoginPage loginPage;
    MyAccountPage myAccountPage;

    public static final By leftMenu = By.id("left-menu");
    public static final By allMenuItems = By.xpath("//li[starts-with(@id,'menu-item-')]");
    public static final By allMenuItemsLinks = By.xpath("//li[starts-with(@class,'menu-item menu-item-type-post_type menu-item-object-namaste_course menu-item-') and not(contains(@class,'menu-item-has-children'))]/a");

    @Before
    public void setUpForCrawler() {
        loginPage = new LoginPage(driver);
        myAccountPage = new MyAccountPage(driver);

        loginPage.navigateToLoginPage(loginPageURL).
                loginAs(username, password);
    }

    @After
    public void tearDownForCrawler() {
        myAccountPage.logoutFromAccount();
    }

    @Test
    public void crawlAllTheURLsTest() {
        myAccountPage.showLeftMenu()
                .collectAllLessonsDirectLinksAndTitlesFromMenu();
    }
}
package com.screenripper.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BasePage {
    org.slf4j.Logger log = LoggerFactory.getLogger(MyAccountPage.class);

    public WebDriver driver;
    public BasePage(WebDriver driver){
        this.driver = driver;
    }

    public BasePage(){
    }



    public By topHeader = By.id("top-header");
    private By mainHeader = By.id("main-header");

    protected String imagePath = "src/test/resources/images/";

    public WebElement findElement(By by) {
        return driver.findElement(by);
    }
    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    public void makeWebElementVisibleInViewport(WebElement element){

        scrollToWebElement(element);

        log.info("Making sure that webElement fits active viewport");

        int viewportHeight = driver.manage().window().getSize().getHeight();
        int topHeaderHeight = driver.findElement(topHeader).getSize().getHeight();
        int mainHeaderHeight = driver.findElement(mainHeader).getSize().getHeight();

        int scrollableViewportSize = viewportHeight - topHeaderHeight - mainHeaderHeight;
        int scrollableViewportUpperBoundary = 1 + topHeaderHeight + mainHeaderHeight;
        int scrollableViewportLowerBoundary = viewportHeight;

        log.info("Scrollable Viewport Size is " + scrollableViewportSize);
        log.info("Scrollable Upper Boundary is " + scrollableViewportUpperBoundary);
        log.info("Scrollable Lower Boundary is " + scrollableViewportLowerBoundary);


        int elementUpperBoundaryLocation = element.getLocation().getY();
        log.info("Element Upper Boundary Location is " + elementUpperBoundaryLocation);

        int elementMiddleLocation = element.getSize().getHeight() / 2 + elementUpperBoundaryLocation;
        int elementLowerBoundaryLocation = elementUpperBoundaryLocation + element.getSize().getHeight();
        log.info("Element Lower Boundary Location is " + elementLowerBoundaryLocation);

        if (scrollableViewportUpperBoundary > elementUpperBoundaryLocation){
            webpageScrollOn(scrollableViewportUpperBoundary - elementUpperBoundaryLocation + 1);
        } else if (scrollableViewportLowerBoundary < elementLowerBoundaryLocation){
            webpageScrollOn(scrollableViewportLowerBoundary - elementLowerBoundaryLocation - 1);
        }
    }

    private void webpageScrollOn(int targetY) {
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("scroll(0, " + targetY + ");");
        log.info("Scrolled to " + targetY);
    }

    protected void scrollToWebElement(WebElement videoFrame) {
        log.info("Scrolling directly to webElement");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", videoFrame);
    }

}

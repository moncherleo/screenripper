package com.screenripper.pages;

import com.automation.remarks.video.exception.RecordingException;
import com.screenripper.tests.webdriver.Lesson;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.sikuli.script.*;
import org.sikuli.script.Button;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import ru.yandex.qatools.allure.annotations.Step;

import static com.screenripper.pages.LoginPage.*;
import static com.screenripper.tests.webdriver.Config.*;
import static com.screenripper.tests.webdriver.URLCrawlerTest.allMenuItemsLinks;
import static org.sikuli.script.Constants.FOREVER;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class MyAccountPage extends BasePage {

    org.slf4j.Logger log = LoggerFactory.getLogger(MyAccountPage.class);

    By lessonsURLs = By.cssSelector(".available-lessons a");
    By leftMenuButton = By.cssSelector(".et_toggle_left_menu");
    By leftMenuContainer = By.id("left-menu");
    By courseTitle = By.cssSelector(".entry-title");
    By videoLengthTime = By.cssSelector(".fp-duration");
    By lectureTitle = By.cssSelector(".entry-title");
    By topicTitle = By.cssSelector(".entry-content .et_pb_tab_0 h3");
    public static By videoFrameLocator = By.cssSelector(".fp-player");


    public MyAccountPage(WebDriver driver) {
        super(driver);
    }

    public MyAccountPage() {
        super();
    }

    @Step("Logging out from the account")
    public void logoutFromAccount() {
        List<WebElement> exitMenuItems;

        if (driver != null) {

            exitMenuItems = findElements(exitMenuItem);

            if (exitMenuItems.size() > 0) {
                exitMenuItems.get(0).click();
            }

            Assert.assertTrue(findElements(LoginPage.loginButton).size() > 0);

        }
    }

    @Step("Navigate to the page by URL \"{0}\"")
    public MyAccountPage navigateToThePageByURL(String courseURL) {
        driver.navigate().to(courseURL);
        log.info("Navigated to the course page by URL: " + courseURL);
        return this;
    }

    @Step("Display left menu")
    public MyAccountPage showLeftMenu() {
        findElement(leftMenuButton).click();
        Assert.assertTrue(driver.findElement(leftMenuContainer).isDisplayed());
        return this;
    }

    @Step("Hide left menu")
    public void hideLeftMenu() {
        findElement(leftMenuButton).click();
        Assert.assertFalse(driver.findElement(leftMenuContainer).isDisplayed());
    }

    @Step("Collect all course titles and direct links from menu")
    public void collectAllLessonsDirectLinksAndTitlesFromMenu() {
        List<String> coursesDirectLinks = new ArrayList<>();
        List<String> coursesTitles = new ArrayList<>();
        String attribute = "href";
        List<Lesson> allTheLessons = new ArrayList<>();

        for (WebElement e : findElements(allMenuItemsLinks)) {
            String courseDirectLink = e.getAttribute(attribute);
            coursesDirectLinks.add(courseDirectLink);
        }

        for (String courseDirectLink : coursesDirectLinks) {
            driver.navigate().to(courseDirectLink);
            String courseTitleString = findElement(courseTitle).getText()
                    .replace(":", "_").replace(',', '_');
            //coursesTitles.add(courseTitleString);

            List<String> lessonsURLsList = new ArrayList<>();
            for (WebElement em : driver.findElements(lessonsURLs)) {
                lessonsURLsList.add(em.getAttribute(attribute));
            }

            for (String lessonURL : lessonsURLsList) {
                driver.navigate().to(lessonURL);
                String lessonTitle = findElement(lectureTitle).getText();
                int numberOfVideos = findElements(videoFrameLocator).size();
                allTheLessons.add(new Lesson(courseTitleString,
                        courseDirectLink,
                        lessonTitle,
                        lessonURL,
                        numberOfVideos));
            }

        }

        writeAllTheLessonsCollectionToCSVFile(allTheLessons, csvFilename);

    }

    public void writeAllTheLessonsCollectionToCSVFile(List<Lesson> allTheLessons, String csvFilename) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(csvFilename));
            writer.write("CourseTitle|CourseURL|LessonTitle|LessonURL|NumberOfVideosOnPage");

            for (Lesson l : allTheLessons) {
                writer.newLine();
                writer.write(l.getCourseTitleURLLessonTitleURLInOneString());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mergeCourseTitleAndCourseDirectLinksAndWriteToFile(List<String> coursesTitles, List<String> coursesDirectLinks) {
        List<String> combinedStrings = new ArrayList<>();
        for (int i = 0; i < coursesTitles.size(); i++) {
            combinedStrings.add(coursesTitles.get(i) + "|" + coursesDirectLinks.get(i));

            writeCourseTitlesAndURLsToCSVfile(combinedStrings, csvFilename);
        }
    }

    public void writeCourseTitlesAndURLsToCSVfile(List<String> stringList, String filename) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            writer.write("Course Title|Course URL");

            for (String s : stringList) {
                writer.newLine();
                writer.write(s);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Step("Get playback time of current video")
    public String getVideoTime(int videoIndex) {

        String currentVideoTimeString = findElements(videoLengthTime).get(videoIndex).getText();
        log.info("Found next video time: " + currentVideoTimeString);

        return currentVideoTimeString;
    }

    @Step("Start HTML5 video playback")
    public MyAccountPage startHTML5VideoPlayback(WebElement webElement) {

        By playButton5 = By.cssSelector(".fp-play");
        By maximizeButton5 = By.cssSelector(".fp-fullscreen");

        webElement.click();
        webElement.findElement(playButton5).click();
        webElement.findElement(maximizeButton5).click();

        return this;
    }

    public void setFocusOnCourseWindow() {
        int secondsDelay = 10;
        int minDelay = 1;

        Screen screen = new Screen();
        screen.setAutoWaitTimeout(secondsDelay);
        ImagePath.add(imagePath);
        try {
            screen.click("phone_icon.png");
        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        }
        log.info("Clicked on phone icon to have focus on browser window");
    }

    @Step("Start video playback")
    public String startVideoPlayback(int videoIndex) {
        String videoTime = "";

        int secondsDelay = 10;
        int minDelay = 1;

        int mouseX = 0;
        int mouseY = 25;

        try {
            Screen screen = new Screen();
            screen.setAutoWaitTimeout(secondsDelay);
            ImagePath.add(imagePath);

            //Settings.MinSimilarity = 0.5;

            if (screen.exists("face1.png", minDelay) != null) {
                screen.click("face1.png");
            } else if (screen.exists("face2.png", minDelay) != null) {
                screen.click("face2.png");
            } else if (screen.exists("face3.png", minDelay) != null) {
                screen.click("face3.png");
            } else if (screen.exists("face4.png", minDelay) != null) {
                screen.click("face4.png");
            } else if (screen.exists("face5.png", minDelay) != null) {
                screen.click("face5.png");
            } else if (screen.exists("face6.png", minDelay) != null) {
                screen.click("face6.png");
            } else if (screen.exists("play_button.png", minDelay) != null) {
                screen.click("play_button.png");
            }
            log.info("Clicked on video player play button");

            Screen screen3 = new Screen();
            screen3.setAutoWaitTimeout(secondsDelay);
            ImagePath.add(imagePath);
            screen3.wait("maximize_button.png");
            screen3.click("maximize_button.png");
            videoTime = getVideoTime(videoIndex);
            log.info("Clicked on video player maximize button");


        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        }

        //move mouse to the almost left top of the screen
        try {

            Robot robot = new Robot();

            robot.mouseMove(mouseX, mouseY);

        } catch (AWTException e) {
            e.printStackTrace();
        }

        log.info("Moved mouse cursor to x:" + mouseX + " y:" + mouseY);

        log.info("Video playback is started");

        return videoTime;
    }

    @Step("Start new ffmpeg screen recording")
    public MyAccountPage startNewVideoRecording() {
        log.info("Trying to start new video recording ...");

        File shellScriptRelativePath = new File("src/test/resources/shell/ffmpeg_capture.sh");
        String bashCommand = "/bin/bash -c ";
        String shellCommand = bashCommand + shellScriptRelativePath.getAbsolutePath();

        log.info("Making shell command for ffmpeg as : " + shellCommand);


        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec(shellCommand);
            log.info("Executing shell command for ffmpeg as : " + shellCommand);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    @Step("Parsing video time string as a value in long format in milliseconds")
    public long parseStringTimeToLongInMilliseconds(String timeString) {
        char separator = ':';

        //original video time format is xx:yy or z:xx:yy
        String[] splitArray = timeString.split(separator + "");

        // minutes:seconds
        if (howManyTimesSeparatorOccurs(separator, timeString) == 1) {
            log.info("Found video time of " + splitArray[0] + " minutes and " + splitArray[1] + " seconds");
            return Integer.parseInt(splitArray[0]) * 60 * 1000
                    + Integer.parseInt(splitArray[1]) * 1000;
        }

        //hours:minutes:seconds
        log.info("Found video time of " + splitArray[0] + " hours and " + splitArray[1] + " minutes and " + splitArray[2] + " seconds");
        return Integer.parseInt(splitArray[0]) * 60 * 60 * 1000
                + Integer.parseInt(splitArray[1]) * 60 * 1000
                + Integer.parseInt(splitArray[2]) * 1000;
    }

    private int howManyTimesSeparatorOccurs(char separator, String string) {
        int counter = 0;
        for (int i = 0; i < string.length(); i++) {
            if (separator == string.charAt(i)) {
                counter++;
            }
        }
        return counter;
    }

    @Step("Stop video recording, exit full screen and kill ffmpeg with delay for {0} milliseconds")
    public MyAccountPage stopVideoRecordingAfterTime(long videoTime, String videoTitle) {
        log.info("Waiting till the video ends for " + videoTime + " milliseconds");
        try {
            Thread.sleep(videoTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        exitFullScreenAndStopCamtasiaRecordingAndNameTheFileAs(videoTitle);
        // exitFullScreenAndKillFFmpeg();

        return this;
    }

    private void exitFullScreenAndStopCamtasiaRecordingAndNameTheFileAs(String videoTitle) {
        exitFullScreen();
        refreshBrowserPageToStopVideo();
        stopCamtasiaRecordingAndNameRecordingAs(videoTitle);
    }

    private void refreshBrowserPageToStopVideo() {
        driver.navigate().refresh();
    }

    private void stopCamtasiaRecordingAndNameRecordingAs(String videoTitle) {
        Screen screen = new Screen();
        ImagePath.add(imagePath);
        screen.setAutoWaitTimeout(5);

        log.info("Trying to save video with the filename: " + videoTitle);

        try {
            log.info("Stopping the video recording");
            screen.wait("camtasia_task_panel_button_rec.png");
            screen.click("camtasia_task_panel_button_rec.png");

            screen.wait("camtasia_stop_recording.png");
            screen.click("camtasia_stop_recording.png");

            log.info("Waiting for the save window displayed");
            screen.wait("camtasia_save_window_title.png");
            log.info("Clicking on the save window title");
            screen.click("camtasia_save_window_title.png");

            screen.wait("save_button.png");
            log.info("Going to type " + videoTitle + "into the input field");
            screen.paste(videoTitle);
            screen.wait("save_button.png");

            log.info("Clicking on the save button");
            screen.click("save_button.png");

        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        }

    }

    @Step("Move temporary recording to the final name as {0} - {1}")
    public void renameVideoFileTo(String finalDestinationPath) {

        log.info("Final absolute video path is: " + finalDestinationPath);

        new File(temporaryFileName).renameTo(new File(finalDestinationPath));
        log.info("Temporary file " + temporaryFileName + " is moved to: " + finalDestinationPath);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String generateVideoTitle(String courseTitle, String lessonTitle) {
        //String finalDestinationPath = recordingsFolder;
        //log.info("Identified destination video folder as :" + recordingsFolder);

//        return finalDestinationPath = finalDestinationPath
//                .concat(courseTitle)
//                .concat("_")
//                .concat(lessonTitle)
//                .concat("_")
//                .concat(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()))
//                .concat(".trec");


        String s = "";
        s = s.concat(courseTitle)
                .concat("_")
                .concat(lessonTitle)
                .concat("_");

        if (s.length() > 130) {
            s = s.substring(0, 130);
        }

        //s = s.concat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        log.info("Generated video title as " + s);

        return s;

    }

    @Step("Get current lesson title")
    public String getLessonTitle() {
        String lessonTitle = findElement(lectureTitle).getText();

        System.out.println("Found next recording name: " + lessonTitle);
        return lessonTitle;
    }

    public void startMultipleVideosPlaybackAndRecordingThenStopAndSave(String courseTitle, String courseURL, String lessonTitle, String lessonURL, String numberOfVideosOnPage, int startingVideoIndex) {
        long delay = 3000L;

        List<WebElement> videoFrames = findAllVideosRegions(videoFrameLocator);

        log.info("Found " + videoFrames.size() + " videos on the page");

        String topicTitleString;

        /*---------|   |---------------------------------------------------------------------------*/
        for (int i = startingVideoIndex; i < Integer.parseInt(numberOfVideosOnPage); i++) {
            /*---------|   |-------------------------------------------------------------------------*/


            // HARD hardcode in the loop!!!!


            log.info("Processing video with index: " + i);

            // makeWebElementVisibleInViewport(videoFrames.get(i));

            WebElement element;
            if (findElements(topicTitle).size() > 0) {
                element = findElements(topicTitle).get(i);
            } else {
                element = findElements(lectureTitle).get(i);
            }
            topicTitleString = element.getText();
            topicTitleString = topicTitleString.replace(':', '_');
            log.info("Found next topic title: " + topicTitleString);

            //startNewVideoRecording();

            startNewVideoRecordingCamtasia();

            setFocusOnCourseWindow();

            // scrolling to specific video as sidebar is hidden now
            scrollToVideoWithDownArrow(i);

            String videoTime = startVideoPlayback(i);
            //stopVideoRecordingWhenVideoEnds();


            String videoTitle = generateVideoTitle(courseTitle, lessonTitle + i + "_" + topicTitleString);
            stopVideoRecordingAfterTime(parseStringTimeToLongInMilliseconds(videoTime) - delay, videoTitle);

            //stopVideoRecordingAfterTime(30000L, videoTitle); //just a debug

            //renameVideoFileTo(videoTitle);

        }
    }

    private void scrollToVideoWithDownArrow(int currentVideoIndex) {
        int numberOfArrowDownsToScrollVideoFrame = 15;
        long delayBetweenScrolling = 100L;

        Screen screen = new Screen();
        screen.mouseMove(0,300);
        log.info("Moved cursor to y:300");

        screen.mouseDown(Button.LEFT);
        screen.mouseUp(Button.LEFT);
        log.info("Clicking at specified point with Sikuli");

//        for (int i = 1; i < currentVideoIndex; i++) {
//            for (int j = 0; i < numberOfArrowDownsToScrollVideoFrame; i++) {
//                try {
//                    Thread.sleep(delayBetweenScrolling);
//                    screen.type(Key.DOWN);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                log.info("Scrolling down with Down Arrow key");
//            }
//        }

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,700)");


    }

    @Step("Start new Camtasia screen recording")
    private void startNewVideoRecordingCamtasia() {
        Screen screen = new Screen();
        ImagePath.add(imagePath);
        screen.setAutoWaitTimeout(30);
        try {
            screen.wait("camtasia_task_panel_button.png");
            screen.click("camtasia_task_panel_button.png");

            screen.wait("camtasia_start_recording.png");
            screen.click("camtasia_start_recording.png");

            Thread.sleep(3000);

            screen.click(screen.exists("camtasia_start_recording_red_b.png", 10), 0);

        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Step("Stop video recording, exit full screen and kill ffmpeg")
    private MyAccountPage stopVideoRecordingWhenVideoEnds() {

        Screen screen = new Screen();
        ImagePath.add(imagePath);

        try {
            screen.wait("play_button_end.png", FOREVER);
        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        }

        exitFullScreenAndKillFFmpeg();

        System.out.println("Video recording stopped");

        return this;
    }

    public void exitFullScreenAndKillFFmpeg() {

        exitFullScreen();

        List<String> args = new ArrayList<>();
        args.add("pkill");
        args.add("-INT");
        args.add("ffmpeg");
        log.info("Making shell command to kill FFmpeg as : " + args.toString());

        try {
            new ProcessExecutor()
                    .command(args)
                    .readOutput(true)
                    .execute()
                    .outputUTF8();
        } catch (IOException | InterruptedException | TimeoutException e) {
            throw new RecordingException(e);
        }

        log.info("FFmpeg now should be dead");

    }

    private void exitFullScreen() {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        log.info("Send ESCAPE key to exit fullscreen");

    }

    public List<WebElement> findAllVideosRegions(By videoFrameLocator) {
        List<WebElement> videoFrames = findElements(videoFrameLocator);

        return videoFrames;
    }
}
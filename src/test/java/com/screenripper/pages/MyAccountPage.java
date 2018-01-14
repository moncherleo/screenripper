package com.screenripper.pages;

import com.automation.remarks.video.exception.RecordingException;
import com.screenripper.tests.webdriver.Lesson;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.sikuli.script.*;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import ru.yandex.qatools.allure.annotations.Step;

import static com.screenripper.pages.LoginPage.*;
import static com.screenripper.tests.webdriver.Config.*;
import static com.screenripper.tests.webdriver.URLCrawlerTest.allMenuItemsLinks;
import static org.sikuli.script.Constants.FOREVER;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    By videoFrameLocator = By.cssSelector(".fp-player");


    public MyAccountPage(WebDriver driver) {
        super(driver);
    }

    public MyAccountPage() {
        super();
    }

    @Step("Logging out from the account")
    public LoginPage logoutFromAccount() {
        findElement(exitMenuItem).click();
        Assert.assertTrue(findElements(LoginPage.loginButton).size() > 0);
        return new LoginPage(driver);
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
                allTheLessons.add(new Lesson(courseTitleString,
                        courseDirectLink,
                        lessonTitle,
                        lessonURL));
            }

        }

        writeAllTheLessonsCollectionToCSVFile(allTheLessons, csvFilename);

    }

    public void writeAllTheLessonsCollectionToCSVFile(List<Lesson> allTheLessons, String csvFilename) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(csvFilename));
            writer.write("CourseTitle|CourseURL|LessonTitle|LessonURL");

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
    public String getVideoTime(WebElement element) {

        String currentVideoTimeString = element.findElement(videoLengthTime).getText();
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


    @Step("Start video playback")
    public String startVideoPlayback(WebElement element) {
        String videoTime = "";

        long delay = 5000L;


        try {
            Screen screen = new Screen();
            screen.setAutoWaitTimeout(30);
            ImagePath.add(imagePath);
            screen.click("phone_icon.png");
            log.info("Clicked on phone icon to have focus on browser window");

            Screen screen2 = new Screen();
            screen2.setAutoWaitTimeout(30);
            ImagePath.add(imagePath);
            //Settings.MinSimilarity = 0.5;
            screen2.wait("play_button.png");
            screen2.click("play_button.png");
            log.info("Clicked on video player play button");

            Screen screen3 = new Screen();
            screen3.setAutoWaitTimeout(30);
            ImagePath.add(imagePath);
            screen3.wait("maximize_button.png");
            videoTime = getVideoTime(element);
            screen3.click("maximize_button.png");
            log.info("Clicked on video player maximize button");


        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        }

        //move mouse to the almost left top of the screen
        try {

            Robot robot = new Robot();
            robot.mouseMove(0, 25);

        } catch (AWTException e) {
            e.printStackTrace();
        }

        log.info("Moved mouse cursor to x:0 y:25");

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
        stopCamtasiaRecordingAndNameRecordingAs(videoTitle);
    }

    private void stopCamtasiaRecordingAndNameRecordingAs(String videoTitle) {
        Screen screen = new Screen();
        ImagePath.add(imagePath);
        screen.setAutoWaitTimeout(5);

        driver.quit();
        log.info("Quit browser... ");

        try {
            screen.wait("camtasia_task_panel_button_rec.png");
            screen.click("camtasia_task_panel_button_rec.png");

            screen.wait("camtasia_stop_recording.png");
            screen.click("camtasia_stop_recording.png");

            screen.wait("camtasia_save_window_title.png");

            screen.wait("save_button.png");
            screen.type(videoTitle);
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
        String finalDestinationPath = recordingsFolder;
        log.info("Identified destination video folder as :" + recordingsFolder);

        return finalDestinationPath = finalDestinationPath
                .concat(courseTitle)
                .concat("_")
                .concat(lessonTitle)
                .concat("_")
                .concat(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()))
                .concat(".trec");
    }

    @Step("Get current lesson title")
    public String getLessonTitle() {
        String lessonTitle = findElement(lectureTitle).getText();

        System.out.println("Found next recording name " + lessonTitle);
        return lessonTitle;
    }

    public void startMultipleVideosPlaybackAndRecordingThenStopAndSave(String courseTitle, String courseURL, String lessonTitle, String lessonURL) {
        long delay = 3000L;

        List<WebElement> videoFrames = findAllVideosRegions(videoFrameLocator);

        log.info("Found " + videoFrames.size() + " videos on the page");

        String topicTitleString = "";

        for (int i = 0; i < videoFrames.size(); i++) {
            log.info("Processing video number " + i);

            makeWebElementVisibleInViewport(videoFrames.get(i));

            WebElement element = findElements(topicTitle).get(i);
            topicTitleString = element.getText();
            topicTitleString = topicTitleString.replace(':', '_');
            log.info("Found next topic title " + topicTitleString);

            //startNewVideoRecording();
            startNewVideoRecordingCamtasia();
            String videoTime = startVideoPlayback(videoFrames.get(i));
            //stopVideoRecordingWhenVideoEnds();

            //stopVideoRecordingAfterTime(parseStringTimeToLongInMilliseconds(videoTime) - delay);

            String videoTitle = generateVideoTitle(courseTitle, lessonTitle + i + "-" + topicTitleString);
            stopVideoRecordingAfterTime(30000L, videoTitle); //just a debug

            //renameVideoFileTo(videoTitle);
        }
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
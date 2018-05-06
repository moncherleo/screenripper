package com.screenripper.tests.webdriver;

import com.screenripper.pages.LoginPage;
import com.screenripper.pages.MyAccountPage;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.mappers.CsvWithHeaderMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.allure.annotations.Parameter;

import java.io.*;

import static com.automation.remarks.video.SystemUtils.runCommand;
import static com.screenripper.tests.webdriver.Config.*;

@RunWith(JUnitParamsRunner.class)
public class RipTheCurlTest extends BaseTest {
    org.slf4j.Logger log = LoggerFactory.getLogger(RipTheCurlTest.class);


    int startingVideoIndex = 1; //first element index is 0

    //mvn test -Dtest=RipTheCurlTest

    LoginPage loginPage;
    MyAccountPage myAccountPage;

    @Parameter("Course title")
    String ct;

    @Parameter("Course URL")
    String cURL;

    @Parameter("Lesson title")
    String lt;

    @Parameter("Lesson URL")
    String lURL;

    @Test
    @FileParameters(value = csvFilename, mapper = CsvWithHeaderMapper.class)
    public void recordTheVideoTest(String courseTitle, String courseURL, String lessonTitle, String lessonURL, String numberOfVideosOnPage) {
        // Архитектор карьеры|https://my.stratoplan.net/course/career-architect/|Часть №1|https://my.stratoplan.net/lesson/career-architect-s01/

//        String courseTitle = "Архитектор карьеры";
//        String courseURL = "https://my.stratoplan.net/course/career-architect/";
//        String lessonTitle = "Часть №1";
//        String lessonURL = "https://my.stratoplan.net/lesson/career-architect-s01/";

        // linking CSV values to Allure parameters
        ct = courseTitle;
        lt = lessonTitle;
        cURL = courseURL;
        lURL = lessonURL;

        // verify if there any unprocessed videos on the page
        if (Integer.parseInt(numberOfVideosOnPage) > startingVideoIndex) {

            new LoginPage(driver).navigateToLoginPage(loginPageURL).
                    closeAutomationChromeAlert().
                    loginAs(username, password);

            new MyAccountPage(driver).navigateToThePageByURL(lessonURL).
            startMultipleVideosPlaybackAndRecordingThenStopAndSave(courseTitle, courseURL, lessonTitle, lessonURL, numberOfVideosOnPage, startingVideoIndex);
            addVideoAsProcessed(courseTitle
                    .concat("|")
                    .concat(courseURL)
                    .concat("|")
                    .concat(lessonTitle)
                    .concat("|")
                    .concat(lessonURL));
        } else {
            Assert.fail("Screen ripper is failed due to request video index "
                    + (startingVideoIndex + 1)
                    + " is greated that current video index "
                    + numberOfVideosOnPage + ".");
        }
    }

    public void addVideoAsProcessed (String processedVideoString) {

        BufferedWriter bw = null;

        try {
            // APPEND MODE SET HERE
            bw = new BufferedWriter(new FileWriter(processedVideosCVSFilename, true));
            bw.write(processedVideoString);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {                       // always close the file
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
                // just ignore it
            }
        } // end try/catch/finally

    } // end method()

}

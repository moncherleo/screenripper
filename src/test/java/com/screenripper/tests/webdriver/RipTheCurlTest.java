package com.screenripper.tests.webdriver;

import com.screenripper.pages.LoginPage;
import com.screenripper.pages.MyAccountPage;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.mappers.CsvWithHeaderMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.yandex.qatools.allure.annotations.Parameter;

import java.io.*;

import static com.automation.remarks.video.SystemUtils.runCommand;
import static com.screenripper.tests.webdriver.Config.*;

@RunWith(JUnitParamsRunner.class)
public class RipTheCurlTest extends BaseTest {

    //mvn test -Dtest=RipTheCurlTest

    LoginPage loginPage = new LoginPage(driver);
    MyAccountPage myAccountPage = new MyAccountPage(driver);

    @Parameter("Course title")
    String ct;

    @Parameter("Course URL")
    String cURL;

    @Parameter("Lesson title")
    String lt;

    @Parameter("Lesson URL")
    String lURL;

    @Before
    public void beforeTest() {
        loginPage = new LoginPage(driver);
        myAccountPage = new MyAccountPage(driver);

//        myAccountPage.startNewVideoRecording();
//
//        try {
//            Thread.sleep(10000000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        loginPage.navigateToLoginPage(loginPageURL).
                closeAutomationChromeAlert().
                loginAs(username, password);
    }

    @After
    public void afterTest() {
    }

    @Test
    @FileParameters(value = csvFilename, mapper = CsvWithHeaderMapper.class)
    public void recordTheVideoTest(String courseTitle, String courseURL, String lessonTitle, String lessonURL, String numberOfVideosOnPage) {
        // Архитектор карьеры|https://my.stratoplan.net/course/career-architect/|Часть №1|https://my.stratoplan.net/lesson/career-architect-s01/

//        String courseTitle = "Архитектор карьеры";
//        String courseURL = "https://my.stratoplan.net/course/career-architect/";
//        String lessonTitle = "Часть №1";
//        String lessonURL = "https://my.stratoplan.net/lesson/career-architect-s01/";

        ct = courseTitle;
        lt = lessonTitle;
        cURL = courseURL;
        lURL = lessonURL;

        myAccountPage.navigateToThePageByURL(lessonURL);
        myAccountPage.startMultipleVideosPlaybackAndRecordingThenStopAndSave(courseTitle, courseURL, lessonTitle, lessonURL, numberOfVideosOnPage);
        addVideoAsProcessed(courseTitle
                .concat("|")
                .concat(courseURL)
                .concat("|")
                .concat(lessonTitle)
                .concat("|")
                .concat(lessonURL));
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

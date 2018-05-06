package com.screenripper.tests.webdriver;

import com.screenripper.pages.MyAccountPage;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.mappers.CsvWithHeaderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static com.screenripper.tests.webdriver.Config.csvVideoFileName;
import static com.screenripper.tests.webdriver.ReadTheFile.readTheFilelist;
import static org.sikuli.script.Constants.FOREVER;

@RunWith(JUnitParamsRunner.class)
public class ConvertTheVideosTest {

    org.slf4j.Logger log = LoggerFactory.getLogger(ConvertTheVideosTest.class);

    String pathToFolder = "/Volumes/FreeDisk/camtasia/";
    String camtasiaExtension = ".trec";
    public String imagePath = "src/test/resources/images/";

    List<String> videoFileNames;

    @Test
    public void convertTheVideos() {

        try {
            videoFileNames = readTheFilelist(csvVideoFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String videoFileName : videoFileNames) {
            videoFileName = pathToFolder + videoFileName;
            System.out.println(videoFileName);

            convertCamtasiaVideoToMP4(videoFileName);

        }

    }

    private void convertCamtasiaVideoToMP4(String videoFileName) {

        openVideoFileInCamtasia(videoFileName);

        List<String> strippableParts = new ArrayList<>();
        strippableParts.add(pathToFolder);
        strippableParts.add(camtasiaExtension);

        String strippedVideoFileName = stripVideoFileName(videoFileName, strippableParts);

        System.out.println(strippedVideoFileName);

        exportVideoInCamtasiaToMP4(strippedVideoFileName);

    }

    private void exportVideoInCamtasiaToMP4(String strippedVideoFileName) {
        long timeout = 240000L;
        long lookupTimeout = 60000L;

        try {
            Screen screen = new Screen();
            screen.setAutoWaitTimeout(timeout/1000);
            ImagePath.add(imagePath);

            screen.wait("camtasia_record_button_small.png");
            screen.wait("camtasia_share_button.png");
            screen.click("camtasia_share_button.png");
            screen.click("camtasia_local_file.png");
            screen.wait("camtasia_export_button.png");
            screen.paste(strippedVideoFileName);
            screen.click("camtasia_export_button.png");

            do {
                try {
                    Thread.sleep(lookupTimeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } while (screen.exists("camtasia_reveal_in_finder_button.png") == null);

            //screen.wait("camtasia_reveal_in_finder_button.png", FOREVER);

            screen.wait("camtasia_close_button.png");
            screen.click("camtasia_close_button.png");
            screen.wait("camtasia_close_window.png");
            screen.click("camtasia_close_window.png");
            screen.wait("camtasia_do_not_save_button.png");
            screen.click("camtasia_do_not_save_button.png");

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        }
    }

    private String stripVideoFileName(String videoFileName, List<String> strippableParts) {
        for (String s : strippableParts) {
            videoFileName = videoFileName.replace(s, "");
        }

        return videoFileName;
    }

    private void openVideoFileInCamtasia(String videoFileName) {
        List<String> args = new ArrayList<>();
        args.add("open");
        args.add(videoFileName);

        System.out.println(args.toString());

        try {
            new ProcessExecutor().command(args).readOutput(true).execute().outputUTF8();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}

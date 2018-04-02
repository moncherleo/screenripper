package com.screenripper.tests.webdriver;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.junit.Test;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.screenripper.tests.webdriver.Config.mp4VideosToTrim;
import static com.screenripper.tests.webdriver.Config.pathToFolder;
import static com.screenripper.tests.webdriver.ReadTheFile.readTheFilelist;

public class TrimTheVideosTest {
    List<String> videoFileNames;

    String ffmpegPath = "/usr/local/bin/ffmpeg";
    String ffprobePath = "/usr/local/bin/ffprobe";

    double movieTailSizeInSeconds = 7;
    double movieExtractionStartInSeconds = 30;


    @Test
    public void trimTheVideosByFFmpeg() {

        FFmpeg ffmpeg = null;
        FFprobe ffprobe = null;

        try {
            ffmpeg = new FFmpeg(ffmpegPath);
            ffprobe = new FFprobe(ffprobePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            videoFileNames = readTheFilelist(mp4VideosToTrim);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String videoFileName : videoFileNames) {
            videoFileName = pathToFolder + videoFileName;

            FFmpegProbeResult probeResult = null;
            try {
                probeResult = ffprobe.probe(videoFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FFmpegFormat format = probeResult.getFormat();
            double mp4Duration = format.duration;
            String extractionStart = formatDateInHHMMSS(movieExtractionStartInSeconds);
            String extractionDuration = formatDateInHHMMSS(mp4Duration - movieExtractionStartInSeconds - movieTailSizeInSeconds);

            extractVideoFragmentInFFmpeg(videoFileName, extractionStart, extractionDuration);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            removeOriginalVideoFile(videoFileName);

        }
    }

    private void removeOriginalVideoFile(String videoFileName) {
        List<String> args = new ArrayList<>();
        //rm -f video.mp4
        args.add("rm");
        args.add("-f");
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

    private String formatDateInHHMMSS(double mp4Duration) {
        Duration duration = Duration.ofSeconds((long) mp4Duration);
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%02d:%02d:%02d.000",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return positive;

    }

    private void extractVideoFragmentInFFmpeg(String videoFileName, String extractionStart, String extractionDuration) {
        List<String> args = new ArrayList<>();
        //ffmpeg -i INFILE.mp4 -vcodec copy -acodec copy -ss 00:01:00.000 -t 00:00:10.000 OUTFILE.mp4
        args.add("ffmpeg");
        args.add("-i");
        args.add(videoFileName);
        args.add("-vcodec");
        args.add("copy");
        args.add("-acodec");
        args.add("copy");
        args.add("-ss");
        args.add(extractionStart);
        args.add("-t");
        args.add(extractionDuration);
        String newVideoFileName = videoFileName.substring(0,videoFileName.length()-4) + "___" + ".mp4";
        args.add(newVideoFileName);

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

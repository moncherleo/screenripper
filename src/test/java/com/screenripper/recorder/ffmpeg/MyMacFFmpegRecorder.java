package com.screenripper.recorder.ffmpeg;

import java.io.File;

/**
 * Created by cherleo on 12/31/17.
 */
public class MyMacFFmpegRecorder extends MyFFMpegRecorder {

    public String absoluteFilePath = "";

    @Override
    public void start() {
        absoluteFilePath = getFfmpegWrapper().startFFmpeg("-vsync", "2");
    }
}
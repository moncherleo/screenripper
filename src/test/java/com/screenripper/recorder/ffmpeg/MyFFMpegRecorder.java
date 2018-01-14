package com.screenripper.recorder.ffmpeg;

import com.automation.remarks.video.exception.RecordingException;
import com.screenripper.recorder.MyVideoRecorder;
import org.awaitility.core.ConditionTimeoutException;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Created by cherleo on 12/31/17.
 */
public abstract class MyFFMpegRecorder extends MyVideoRecorder {

    private MyFFmpegWrapper ffmpegWrapper;

    public MyFFMpegRecorder() {
        this.ffmpegWrapper = new MyFFmpegWrapper();
    }

    public MyFFmpegWrapper getFfmpegWrapper() {
        return ffmpegWrapper;
    }

    @Override
    public File stopAndSave(final String filename) {
        File file = getFfmpegWrapper().stopFFmpegAndSave(filename);
        waitForVideoCompleted(file);
        setLastVideo(file);
        return file;
    }

    private void waitForVideoCompleted(File video) {
        try {
            await().atMost(5, TimeUnit.SECONDS)
                    .pollDelay(1, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until(video::exists);
        } catch (ConditionTimeoutException ex) {
            throw new RecordingException(ex.getMessage());
        }
    }
}

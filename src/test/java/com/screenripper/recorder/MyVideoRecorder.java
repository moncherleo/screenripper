package com.screenripper.recorder;

import com.screenripper.recorder.interfaces.IMyVideoRecorder;
import com.screenripper.recorder.interfaces.MyVideoConfiguration;
import org.aeonbits.owner.ConfigFactory;

import java.io.File;

import static com.automation.remarks.video.SystemUtils.getOsType;

/**
 * Created by cherleo on 12/31/17.
 */
public abstract class MyVideoRecorder implements IMyVideoRecorder {
    public static MyVideoConfiguration conf() {
        ConfigFactory.setProperty("os.type", getOsType());
        return ConfigFactory.create(MyVideoConfiguration.class, System.getProperties());
    }

    private static File lastVideo;

    protected void setLastVideo(File video) {
        lastVideo = video;
    }

    public static File getLastRecording() {
        return lastVideo;
    }
}

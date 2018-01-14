package com.screenripper.recorder.interfaces;

import com.automation.remarks.video.SystemUtils;
import com.automation.remarks.video.enums.RecorderType;
import com.automation.remarks.video.enums.RecordingMode;
import com.automation.remarks.video.enums.VideoSaveMode;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

import java.awt.*;
import java.io.File;

/**
 * Created by cherleo on 12/31/17.
 */
@LoadPolicy(LoadType.MERGE)
@Sources({ "classpath:video.properties",
        "classpath:myffmpeg-${os.type}.properties" })
public interface MyVideoConfiguration extends Config {

    @Key("video.folder")
    default String folder() {
        return System.getProperty("user.dir") + File.separator + "video";
    }

    @Key("video.enabled")
    @DefaultValue("true")
    Boolean videoEnabled();

    @Key("video.mode")
    @DefaultValue("ANNOTATED")
    RecordingMode mode();

    @DefaultValue("http://localhost:4444")
    @Key("remote.video.hub")
    String remoteUrl();

    @Key("recorder.type")
    @DefaultValue("FFMPEG")
    RecorderType recorderType();

    @Key("video.save.mode")
    @DefaultValue("ALL")
    VideoSaveMode saveMode();

    @DefaultValue("24")
    @Key("video.frame.rate")
    int frameRate();

    @Key("video.screen.size")
    default Dimension screenSize() {
        return SystemUtils.getSystemScreenDimension();
    }

    @Key("ffmpeg.format")
    String ffmpegFormat();

    @Key("ffmpeg.display")
    String ffmpegDisplay();

    @Key("ffmpeg.encoding.quality")
    String ffmpegEncQuality();
}

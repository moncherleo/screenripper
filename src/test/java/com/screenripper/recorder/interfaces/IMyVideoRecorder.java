package com.screenripper.recorder.interfaces;

import java.io.File;


/**
 * Created by cherleo on 12/31/17.
 */
public interface IMyVideoRecorder {
    void start();

    File stopAndSave(String filename);
}

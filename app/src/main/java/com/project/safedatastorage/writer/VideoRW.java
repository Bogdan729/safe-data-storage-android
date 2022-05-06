package com.project.safedatastorage.writer;

import android.os.Environment;

import java.io.File;
import java.util.List;

public class VideoRW implements FileReaderWriter {
    public static final String VIDEO_DIRECTORY = Environment.getExternalStorageDirectory().getPath()
            + "/DataStorage/video";

    @Override
    public void writeToInternalStorage(File file) {

    }

    @Override
    public List<File> readFromInternalStorage() {
        return null;
    }
}
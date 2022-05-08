package com.project.safedatastorage.writer;

import android.os.Environment;

import java.io.File;
import java.util.List;

public class AudioRW implements FileReaderWriter {
    public static final String AUDIO_DIRECTORY = Environment.getExternalStorageDirectory().getPath()
            + "/DataStorage/audio";

    @Override
    public void writeToInternalStorage(File file) {

    }

    @Override
    public List<File> readFromInternalStorage() {
        return null;
    }
}
package com.project.safedatastorage.writer;

import android.os.Environment;

import java.io.File;
import java.util.List;

public class DocumentRW implements FileReaderWriter {
    public static final String DOCUMENT_DIRECTORY = Environment.getExternalStorageDirectory().getPath()
            + "/DataStorage/documents";

    @Override
    public void writeToInternalStorage(File file) {

    }

    @Override
    public List<File> readFromInternalStorage() {
        return null;
    }
}
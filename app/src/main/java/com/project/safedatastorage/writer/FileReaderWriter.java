package com.project.safedatastorage.writer;

import java.io.File;
import java.util.List;

public interface FileReaderWriter {
    void writeToInternalStorage(File file);
    List<File> readFromInternalStorage();
}
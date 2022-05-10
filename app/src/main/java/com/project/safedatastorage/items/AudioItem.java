package com.project.safedatastorage.items;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.project.safedatastorage.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class AudioItem {

    private final Uri uri;
    private final String name;
    private final String duration;
    private final String size;
    private final File file;

    public AudioItem(Uri uri, String name, String duration, String size, File file) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.file = file;
    }

    public Uri getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getSize() {
        return size;
    }

    public File getFile() {
        return file;
    }

    public static AudioItem createAudio(Context context, Uri audioUri) {
        AudioItem audioItem = null;

        String[] projection = new String[] {
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
        };

        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                audioUri,
                projection,
                null,
                null,
                sortOrder
        )) {
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                String size = FileUtil.getFormattedFileSize(cursor.getInt(sizeColumn));
                String duration = FileUtil.getDurationFromUri(context, audioUri);
                File audioFile = FileUtil.getFileFromUri(context, audioUri);

                audioItem = new AudioItem(audioUri, name, duration, size, audioFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return audioItem;
    }
}

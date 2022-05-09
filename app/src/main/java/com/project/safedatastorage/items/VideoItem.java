package com.project.safedatastorage.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Size;

import com.project.safedatastorage.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VideoItem {

    private final Uri uri;
    private final String name;
    private final String duration;
    private final String size;
    private final File file;
    private final Bitmap thumbnail;

    public VideoItem(Uri uri, String name, String duration, String size, File file, Bitmap thumbnail) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.thumbnail = thumbnail;
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

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public static VideoItem createVideo(Context context, Uri videoUri) {
        VideoItem videoItem = null;

        String[] projection = new String[] {
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
        };

        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                videoUri,
                projection,
                null,
                null,
                sortOrder
        )) {
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                String size = FileUtil.getFormattedFileSize(cursor.getInt(sizeColumn));
                String duration = getVideoDurationFromUri(context, videoUri);
                File videoFile = FileUtil.getFileFromUri(context, videoUri);
                Bitmap thumbnail = FileUtil.createVideoThumbnailFromFile(videoFile);

                videoItem = new VideoItem(videoUri, name, duration, size, videoFile, thumbnail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoItem;
    }

    public static String getVideoDurationFromUri(Context context, Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, videoUri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMills = Long.parseLong(time);
        retriever.release();

        @SuppressLint("DefaultLocale") String res = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeInMills),
                TimeUnit.MILLISECONDS.toSeconds(timeInMills) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMills)));

        return res;
    }
}
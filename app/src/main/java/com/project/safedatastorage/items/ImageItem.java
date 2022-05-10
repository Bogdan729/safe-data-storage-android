package com.project.safedatastorage.items;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.util.ImageUtil;

import java.io.File;
import java.io.IOException;

public class ImageItem {

    private final Uri uri;
    private final String name;
    private final String size;
    private final File file;
    private final Bitmap thumbnail;

    public ImageItem(Uri uri, String name, String size, File file, Bitmap thumbnail) {
        this.uri = uri;
        this.name = name;
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

    public String getSize() {
        return size;
    }

    public File getFile() {
        return file;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public static ImageItem createImage(Context context, Uri imgUri) {
        ImageItem imgItem = null;

        String[] projection = new String[]{
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
        };

        String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                imgUri,
                projection,
                null,
                null,
                sortOrder
        )) {
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                String size = FileUtil.getFormattedFileSize(cursor.getInt(sizeColumn));
                File imgFile = FileUtil.getFileFromUri(context, imgUri);

                Bitmap bitmap = ImageUtil.getThumbnail(imgFile);
                int necessaryRotation = FileUtil.getFileExifRotation(imgFile);
                Bitmap thumbnail = ImageUtil.rotateImage(bitmap, necessaryRotation);

                imgItem = new ImageItem(imgUri, name, size, imgFile, thumbnail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgItem;
    }
}
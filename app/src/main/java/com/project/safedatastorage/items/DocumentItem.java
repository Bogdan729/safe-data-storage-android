package com.project.safedatastorage.items;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.util.PdfUtil;

import java.io.File;
import java.io.IOException;

public class DocumentItem {

    private final Uri uri;
    private final String name;
    private final String size;
    private final File file;
    private final Bitmap thumbnail;

    public DocumentItem(Uri uri, String name, String size, File file, Bitmap thumbnail) {
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

    public static DocumentItem createDocument(Context context, Uri docUri) {
        DocumentItem docItem = null;

        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
        };

        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                docUri,
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
                File docFile = FileUtil.getFileFromUri(context, docUri);
                Bitmap thumbnail = PdfUtil.generateThumbnailFromPdf(docFile.getPath());

                docItem = new DocumentItem(docUri, name, size, docFile, thumbnail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return docItem;
    }

    @Override
    public String toString() {
        return "DocumentItem{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", file=" + file +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
package com.project.safedatastorage.interaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class FileOpener {
    public static void openFile(Context context, File file) throws IOException {
        Uri uri = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (uri.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if (uri.toString().contains(".mp3") || uri.toString().contains(".wav")) {
            intent.setDataAndType(uri, "audio/mpeg");
        } else if (uri.toString().toLowerCase().contains(".jpeg")
                || uri.toString().toLowerCase().contains(".jpg")
                || uri.toString().toLowerCase().contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if (uri.toString().contains(".mp4") || uri.toString().contains(".wav")) {
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
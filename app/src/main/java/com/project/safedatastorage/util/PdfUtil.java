package com.project.safedatastorage.util;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PdfUtil {
    public static Bitmap generateThumbnailFromPdf(String filePath) {
        Bitmap bitmap;
        File file = new File(filePath);

        ParcelFileDescriptor mFileDescriptor = null;

        try {
            mFileDescriptor = ParcelFileDescriptor.open(file,
                    ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PdfRenderer mPdfRenderer = null;
        try {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(0);
        bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(),
                mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);

        mCurrentPage.render(bitmap, null, null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        mCurrentPage.close();
        mPdfRenderer.close();

        try {
            mFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
package com.project.safedatastorage.writer;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.security.Magma;
import com.project.safedatastorage.util.FileUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.SecretKey;

public class ImageRW implements FileReaderWriter {
    private Context context;
    private final Key keyObj;

    public static final File IMAGE_DIRECTORY = new File(Environment.getExternalStorageDirectory().toString());

    public ImageRW(Context context, Key keyObj) {
        this.context = context;
        this.keyObj = keyObj;
    }

    @Override
    public void writeToInternalStorage(File fileItem) {
        FileOutputStream fos = null;
        ByteArrayInputStream stream = null;
        BufferedOutputStream bos = null;

        SecretKey secretKey = keyObj.getSecretKey();

        try {
            File file = new File(IMAGE_DIRECTORY.getPath() + "/DataStorage/images", fileItem.getName());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                byte[] fileBytes = Files.readAllBytes(fileItem.toPath());
                byte[] cipherText = Magma.encrypt(secretKey, fileBytes);

                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);

                stream = new ByteArrayInputStream(cipherText);

                final int BUFFER = 1024;
                int count;
                byte data[] = new byte[BUFFER];

                while ((count = stream.read(data, 0, BUFFER)) != -1)
                    bos.write(data, 0, count);

                bos.flush();
                bos.close();

                // add image to gallery
//                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
//                        file.getName(), file.getName());
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (bos != null)
                    bos.close();
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<File> readFromInternalStorage() {
        List<File> decryptedFiles = new ArrayList<>();

        FileOutputStream fos = null;
        ByteArrayInputStream stream = null;
        BufferedOutputStream bos = null;

        SecretKey secretKey = keyObj.getSecretKey();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try (Stream<Path> paths = Files.walk(Paths.get(IMAGE_DIRECTORY.getPath() + "/DataStorage/images"))) {
                List<File> filesInFolder = paths
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                for (File file : filesInFolder) {

                    // File fileResult = new File(IMAGE_DIRECTORY.getPath() + "/DataStorage/images", "dec_" + file.getName());

                    String[] splitName = FileUtil.splitFileName(file.getName());
                    File tempFile = File.createTempFile(splitName[0], splitName[1]);
                    tempFile.deleteOnExit();


                    fos = new FileOutputStream(tempFile);
                    bos = new BufferedOutputStream(fos);

                    byte[] fileBytes;
                    fileBytes = Files.readAllBytes(file.toPath());
                    byte[] decryptedText = Magma.decrypt(secretKey, fileBytes);

                    stream = new ByteArrayInputStream(decryptedText);

                    final int BUFFER = 1024;
                    int count;
                    byte data[] = new byte[BUFFER];

                    while ((count = stream.read(data, 0, BUFFER)) != -1)
                        bos.write(data, 0, count);

                    bos.flush();
                    bos.close();

                    decryptedFiles.add(tempFile);
                }
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                    if (stream != null)
                        stream.close();
                    if (bos != null)
                        bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return decryptedFiles;
    }
}
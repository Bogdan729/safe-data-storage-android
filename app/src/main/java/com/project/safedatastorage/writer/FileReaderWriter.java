package com.project.safedatastorage.writer;

import android.content.Context;

import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.security.Kuznechik;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
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

public class FileReaderWriter {

    private static List<File> decryptedFiles;

    public static void writeToInternalStorage(File fileItem, Key keyObj, String directory) {
        FileOutputStream fos = null;
        ByteArrayInputStream stream = null;
        BufferedOutputStream bos = null;

        SecretKey secretKey = keyObj.getSecretKey();

        try {
            File file = new File(directory, fileItem.getName());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                byte[] fileBytes = Files.readAllBytes(fileItem.toPath());
                byte[] cipherText = Kuznechik.encrypt(secretKey, fileBytes);

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

    public static List<File> readFromInternalStorage(Context context, Key keyObj, String directory) {
        decryptedFiles = new ArrayList<>();

        FileOutputStream fos = null;
        ByteArrayInputStream stream = null;
        BufferedOutputStream bos = null;

        SecretKey secretKey = keyObj.getSecretKey();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
                List<File> filesInFolder = paths
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                for (File file : filesInFolder) {
                    File tempFile  = new File(context.getCacheDir(), file.getName());
                    tempFile.deleteOnExit();

                    fos = new FileOutputStream(tempFile);
                    bos = new BufferedOutputStream(fos);

                    byte[] fileBytes;
                    fileBytes = Files.readAllBytes(file.toPath());
                    byte[] decryptedText = Kuznechik.decrypt(secretKey, fileBytes);

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

    public static void clearCache() {
        if (decryptedFiles != null && decryptedFiles.size() > 0)
            for (File file: decryptedFiles)
                file.delete();
    }
}
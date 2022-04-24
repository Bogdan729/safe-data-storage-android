package com.project.safedatastorage.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encoder {
    private static final String ALGO_IMAGE_ENCRYPTOR = "AES/CBC/PKCS5Padding";
    private static final String ALGO_SECRET_KEY = "AES";
    private static final int READ_WRITE_BLOCK_BUFFER = 1024;

    public static void encryptToFile(String keyString, String specStr, InputStream in, OutputStream out) throws IOException {
        try {
            IvParameterSpec iv = new IvParameterSpec(specStr.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8), ALGO_SECRET_KEY);

            Cipher cipher = Cipher.getInstance(ALGO_IMAGE_ENCRYPTOR);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            out = new CipherOutputStream(out, cipher);

            int count = 0;
            byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];

            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0 , count);

            }
        } catch (Exception e ) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    public static void decryptToFile(String keyString, String specStr, InputStream in, OutputStream out) throws IOException {
        try {
            IvParameterSpec iv = new IvParameterSpec(specStr.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8), ALGO_SECRET_KEY);

            Cipher cipher = Cipher.getInstance(ALGO_IMAGE_ENCRYPTOR);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            out = new CipherOutputStream(out, cipher);

            int count = 0;
            byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];

            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0 , count);
            }
        } catch (Exception e ) {
            System.out.println(e);
        } finally {
            out.close();
        }
    }
}

package com.project.safedatastorage.security;

import android.annotation.SuppressLint;
import android.os.Build;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Magma {
    public static final String provider = BouncyCastleProvider.PROVIDER_NAME;

    static {
        Security.removeProvider("BC");
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] encrypt(SecretKey key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("GOST28147/ECB/PKCS5Padding", provider);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(SecretKey key, byte[] cipherText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("GOST28147/ECB/PKCS5Padding", provider);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    public static SecretKey getSecretKey(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256", provider);
        SecretKey hmacKey = keyFact.generateSecret(new PBEKeySpec(password.toCharArray(),
                Hex.decode("0102030405060708090a0b0c0d0e0f10"),
                1024,
                256));
        return new SecretKeySpec(hmacKey.getEncoded(), "GOST28147");
    }


    public static byte[] masterKeyGenerator() throws Exception {
        String password = "password";
        String text = "text";

        SecureRandom randomGenerator = new SecureRandom();

        byte[] salt = randomGenerator.generateSeed(64);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey passwordKey = f.generateSecret(keySpec);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            System.out.println(Base64.getEncoder().encode(password.getBytes()).length);
        }


        PBEParameterSpec parSpec = new PBEParameterSpec(salt, 65536);
        Cipher cipher = Cipher.getInstance("GOST28147/ECB/PKCS5Padding", provider);
        cipher.init(Cipher.ENCRYPT_MODE, passwordKey, parSpec);


        return cipher.doFinal(text.getBytes());
    }
}

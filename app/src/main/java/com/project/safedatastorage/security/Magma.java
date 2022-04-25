package com.project.safedatastorage.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.GeneralSecurityException;
import java.security.Security;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;

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
}

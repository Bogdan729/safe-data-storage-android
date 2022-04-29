package com.project.safedatastorage.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Key implements Serializable {
    public static final String provider = BouncyCastleProvider.PROVIDER_NAME;

    final SecretKey secretKey;
    String password;

    static {
        Security.removeProvider("BC");
        Security.addProvider(new BouncyCastleProvider());
    }

    public Key(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        this.password = password;
        secretKey = getSecretKey(this.password);
    }

    public SecretKey getSecretKey(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBKDF2withHmacGOST3411", provider);
        SecretKey hmacKey = keyFact.generateSecret(new PBEKeySpec(password.toCharArray(),
                Hex.decode("0102030405060708090a0b0c0d0e0f10"),
                1024,
                256));
        return new SecretKeySpec(hmacKey.getEncoded(), "GOST28147");
    }

    public static byte[] getPasswordHash512(String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException  {
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBKDF2withHmacGOST3411", provider);
        SecretKey hmacKey = keyFact.generateSecret(new PBEKeySpec(password.toCharArray(),
                Hex.decode("0102030405060708090a0b0c0d0e0f10"),
                1024,
                512));
        return hmacKey.getEncoded();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
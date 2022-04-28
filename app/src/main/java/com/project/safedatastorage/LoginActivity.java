package com.project.safedatastorage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.project.safedatastorage.security.Key;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public static final String HASH_FILE_NAME = "hash.txt";

    EditText passwordEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button entry = findViewById(R.id.entry_btn);

        passwordEntry = findViewById(R.id.password_entry);

        entry.setOnClickListener(view -> {
            if (isPasswordFilled()) {
                Log.d(TAG, "isPasswordFilled : " + "true");

                String password = passwordEntry.getText().toString();
                byte[] hash = readHash(view);

                try {
                    byte[] hashPassword = Key.getPasswordHash512(password);

                    if (Arrays.equals(hashPassword, hash)) {
                        Key keyObj = new Key(password);

                        // Передать секретный ключ в контейнер
                        Log.d(TAG, "pass correct!");
                        Log.d(TAG, "password secret key " + new String(keyObj.getSecretKey().getEncoded()));

                        Intent intent = new Intent("com.project.safedatastorage.ContainerActivity");
                        startActivity(intent);
                    } else {
                        passwordEntry.setError("Неверный пароль");
                    }
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public byte[] readHash(View v) {
        byte[] hash = null;

        try {
            File hashFile = new File(getFilesDir() + "/" + HASH_FILE_NAME);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                hash = Files.readAllBytes(hashFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hash;
    }

    public boolean isPasswordFilled() {
        String password = passwordEntry.getText().toString();

        return !TextUtils.isEmpty(password);
    }
}
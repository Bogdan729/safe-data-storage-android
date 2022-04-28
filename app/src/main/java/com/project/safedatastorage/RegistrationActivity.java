package com.project.safedatastorage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.safedatastorage.security.Key;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    public static final String HASH_FILE_NAME = "hash.txt";

    private EditText setPasswordEditText;
    private EditText repeatPasswordEditText;

    Key keyObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setPasswordEditText = findViewById(R.id.et_set_password);
        repeatPasswordEditText = findViewById(R.id.et_repeat_password);

        Button setPasswordBtn = findViewById(R.id.btn_set_password);

        setPasswordBtn.setOnClickListener(view -> {
            if (isPasswordsEqual()) {
                Log.d(TAG, "isPasswordsEqual : " + "true");

                savePasswordHash(view);
                creteDefaultDir();

                Intent intent = new Intent("com.project.safedatastorage.ContainerActivity");
                startActivity(intent);
            }
        });
    }

    public void savePasswordHash(View v) {
        FileOutputStream fos = null;

        try {
            String password = setPasswordEditText.getText().toString();
            keyObj = new Key(password);
            byte[] pas512 = keyObj.getPasswordHash512(password);

            Log.d(TAG, "onCreate: hash created : " + new String(pas512));

            fos = openFileOutput(HASH_FILE_NAME, MODE_PRIVATE);
            fos.write(pas512);

            Toast.makeText(getApplicationContext(), "Saved to " + getFilesDir() + "/" + HASH_FILE_NAME, Toast.LENGTH_LONG).show();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isPasswordsEqual() {
        String pas1 = setPasswordEditText.getText().toString();
        String pas2 = repeatPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(pas1) && TextUtils.isEmpty(pas2)) {
            repeatPasswordEditText.setError("Заполните поля");
            return false;
        }

        if (pas1.equals(pas2)) {
            return true;
        } else {
            repeatPasswordEditText.setError("Введенные пароли не совпадают");
            return false;
        }
    }

    public void creteDefaultDir() {
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/DataStorage");

        if (!dir.exists()) {
            dir.mkdir();

            if (dir.isDirectory()) {
                File images = new File(dir, "images");
                File documents = new File(dir, "documents");
                File video = new File(dir, "video");
                File audio = new File(dir, "audio");

                images.mkdir();
                documents.mkdir();
                video.mkdir();
                audio.mkdir();

                Log.d(TAG, "creteDefaultDir: Directory created");
            } else {
                Log.d(TAG, "creteDefaultDir: Error -> Directory isn't created");
            }
        }
    }
}
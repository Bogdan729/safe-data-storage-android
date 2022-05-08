package com.project.safedatastorage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isDefaultDirExists()) {
            Intent LoginIntent = new Intent("com.project.safedatastorage.LoginActivity");
            startActivity(LoginIntent);
        } else {
            Intent RegistrationIntent = new Intent("com.project.safedatastorage.RegistrationActivity");
            startActivity(RegistrationIntent);
        }

        // ТЕСТИРОВАНИЕ ИНИЦИАЛИЗАЦИИ ХРАНИЛИЩА

//        setPasswordBtn.setOnClickListener(view -> {
//            Intent intent = new Intent("com.project.safedatastorage.RegistrationActivity");
//            startActivity(intent);

//                try {
//                    String password = setPasswordEditText.getText().toString();
//
//                    setPasswordEditText.getText().clear();
//                    repeatPasswordEditText.getText().clear();
//
//                    keyObj = new Key(password);
//                    SecretKey secretKey = keyObj.getSecretKey();
//                    String message = "test";
//
//                    byte[] cipherText = Magma.encrypt(secretKey, message.getBytes());
//                    Log.d(TAG, "onCreate: " + new String(cipherText));
//
//                    byte[] decodedText = Magma.decrypt(secretKey, cipherText);
//                    Log.d(TAG, "onCreate: " + new String(decodedText));
//                } catch (GeneralSecurityException e) {
//                    e.printStackTrace();
//                }

//        });

        // ТЕСТИРОВАНИЕ ГЕНЕРАЦИИ КЛЮЧА НА ОСНОВЕ ВВЕДЕННОГО ПАРОЛЯ

//        setContentView(R.layout.activity_main);
//
//        Button button = findViewById(R.id.buttonLoadPicture);
//        input = findViewById(R.id.password);
//
//        Dexter.withContext(this)
//                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        creteDefaultDir();
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        Toast.makeText(MainActivity.this, "You must enable permissions", Toast.LENGTH_SHORT).show();
//                    }
//                }).check();
//
//        button.setOnClickListener(view -> {
//            testEncAndDec();
//        });

        // ОСНОВНАЯ ЛОГИКА
    }

    public boolean isDefaultDirExists() {
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/DataStorage");

        return dir.exists();
    }
}
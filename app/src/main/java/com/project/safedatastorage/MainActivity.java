package com.project.safedatastorage;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

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

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (isDefaultDirExists()) {
                            Intent LoginIntent = new Intent("com.project.safedatastorage.LoginActivity");
                            startActivity(LoginIntent);
                        } else {
                            Intent RegistrationIntent = new Intent("com.project.safedatastorage.RegistrationActivity");
                            startActivity(RegistrationIntent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "Пожалуйста, предоставьте права приложению", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    public boolean isDefaultDirExists() {
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/DataStorage");
        return dir.exists();
    }
}
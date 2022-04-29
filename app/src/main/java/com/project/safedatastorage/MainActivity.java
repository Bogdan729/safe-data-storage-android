package com.project.safedatastorage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.project.safedatastorage.dao.DataConverter;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.security.Magma;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String HASH_FILE_NAME = "hash.txt";

    File internalStorage = new File(Environment.getExternalStorageDirectory().toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent LoginIntent = new Intent("com.project.safedatastorage.ContainerActivity");
//        startActivity(LoginIntent);

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

    // ТЕСТОВЫЕ МЕТОДЫ

    public boolean deleteHashFile() {
        File file = new File(getFilesDir() + "/" + HASH_FILE_NAME);
        boolean deleted = false;

        if (file.delete())
            deleted = true;

        return deleted;
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
//        try {
//            fis = openFileInput(HASH_FILE_NAME);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            String hash;
//
//            while ((hash = br.readLine()) != null) {
//                sb.append(hash).append("\n");
//            }
//
//            return sb.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

//        return sb.toString();
    }

    void testEncAndDec() {

        long time = System.nanoTime();

        try {
            Key keyObj = new Key("test");
            SecretKey key = keyObj.getSecretKey();

            Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_image);
            Bitmap bitmap = DataConverter.drawableToBitmap(drawable);

            // ByteArrayOutputStream звено в преобразовании данных в byte[]
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            // объявление потоков для записи/чтения
            InputStream is = new ByteArrayInputStream(stream.toByteArray());
            File fileOutEnc = new File(internalStorage.getPath() + "/DataStorage/images", "encrypted");
            FileOutputStream out = new FileOutputStream(fileOutEnc);


            File fileOutDec = new File(internalStorage.getPath() + "/DataStorage/images", "decrypted.jpg");
            FileOutputStream outDec = new FileOutputStream(fileOutDec);

            byte[] message = stream.toByteArray();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                byte[] cipherText = Magma.encrypt(key, message);

                // Попытка записи результата ШИФРОВАНИЯ
                Files.write(fileOutEnc.toPath(), cipherText);

//                    int count = 0;
//                    byte[] buffer = new byte[1024];
//
//                    while ((count = is.read(buffer)) > 0) {
//                        out.write(buffer, 0 , count);
//                    }
//
//                    out.close();

                // --------------------------------------

//                    System.out.println("Encrypt : " + new String(cipherText));

                byte[] decrypt = Magma.decrypt(key, cipherText);

                // Попытка записи результата ДЕШИФРОВАНИЯ

                Files.write(fileOutDec.toPath(), decrypt);

//                    int countDec = 0;
//                    byte[] bufferDec = new byte[1024];
//
//                    while ((countDec = is.read(bufferDec)) > 0) {
//                        outDec.write(bufferDec, 0 , countDec);
//                    }
//
//                    outDec.close();

                // --------------------------------------

//                    System.out.println("Decrypt : " + new String(decrypt));

                time = System.nanoTime() - time;
                System.out.printf("Elapsed %,9.3f ms\n", time / 1_000_000.0);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
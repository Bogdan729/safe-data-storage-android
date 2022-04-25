package com.project.safedatastorage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.project.safedatastorage.dao.DataConverter;
import com.project.safedatastorage.fragments.FragmentFile;
import com.project.safedatastorage.fragments.FragmentImage;
import com.project.safedatastorage.fragments.FragmentVideo;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.security.Magma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;
    private EditText input;

    String[] listTitle = {"Фото", "Документы", "Видео"};

    File internalStorage = new File(Environment.getExternalStorageDirectory().toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test_fargments);

        // ТЕСТИРОВАНИЕ

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

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);

        adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new FragmentImage());
        adapter.addFragment(new FragmentFile());
        adapter.addFragment(new FragmentVideo());

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(
                tabLayout,
                viewPager2,
                (tab, position) -> tab.setText(listTitle[position])
        ).attach();
    }

    void creteDefaultDir() {
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


        // ТЕСТОВЫЕ МЕТОДЫ


    void testEncAndDec() {

        long time = System.nanoTime();

        try {
            Key keyObj = new Key(input.getText().toString());
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
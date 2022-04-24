package com.project.safedatastorage;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.project.safedatastorage.security.Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;

    String[] listTitle = {"Фото", "Документы", "Видео"};

    String myKey = "VvTlGRzwnI6GimRi";
    String mySpecKey = "c8qhIC3uGSWZz48C";

    File internalStorage = new File(Environment.getExternalStorageDirectory().toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_test_fargments);

        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.buttonLoadPicture);

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        creteDefaultDir();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "You must enable permissions", Toast.LENGTH_SHORT).show();
                    }
                }).check();

        button.setOnClickListener(view -> {
            // ШИФРОВАНИЕ
            Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_image);
            Bitmap bitmap = DataConverter.drawableToBitmap(drawable);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            InputStream is = new ByteArrayInputStream(stream.toByteArray());

            File fileOutEnc = new File(internalStorage.getPath() + "/DataStorage/images", "encrypted");

            try {
                Encoder.encryptToFile(myKey, mySpecKey, is, new FileOutputStream(fileOutEnc));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ДЕШИФРОВАНИЕ

            File fileOutDec = new File(internalStorage.getPath() + "/DataStorage/images", "decrypted.jpg");

            try {
                Encoder.decryptToFile(myKey, mySpecKey, new FileInputStream(fileOutEnc),
                        new FileOutputStream(fileOutDec));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

//        tabLayout = findViewById(R.id.tab_layout);
//        viewPager2 = findViewById(R.id.view_pager);
//
//        adapter = new ViewPagerAdapter(this);
//        adapter.addFragment(new FragmentImage());
//        adapter.addFragment(new FragmentFile());
//        adapter.addFragment(new FragmentVideo());
//
//        viewPager2.setAdapter(adapter);
//
//        new TabLayoutMediator(
//                tabLayout,
//                viewPager2,
//                (tab, position) -> tab.setText(listTitle[position])
//        ).attach();


    }


    // ТЕСТОВЫЕ МЕТОДЫ

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

//        File file = new File(Environment.getExternalStorageDirectory().toString() + "/saved_images", "test");
//        if (file.exists()) {
//            Toast.makeText(getApplicationContext(), "Directory is already exist", Toast.LENGTH_SHORT).show();
//        } else {
//            file.mkdir();
//            if (file.isDirectory()) {
//                Toast.makeText(getApplicationContext(), "Directory created", Toast.LENGTH_LONG).show();
//            } else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                String message = "Message : failed to create directory" +
//                        "\nPath : " + Environment.getExternalStorageDirectory().toString() +
//                        "\nmkdirs : " + file.mkdir();
//                builder.setMessage(message);
//                builder.show();
//            }
//        }
    }
}
package com.project.safedatastorage;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.safedatastorage.fragments.FragmentFile;
import com.project.safedatastorage.fragments.FragmentImage;
import com.project.safedatastorage.fragments.FragmentVideo;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;

    String[] listTitle = {"Фото", "Документы", "Видео"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_test_fargments);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.buttonLoadPicture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if  (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    createFolder();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createFolder();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    void createFolder() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/saved_images", "test");
        if (file.exists()) {
            Toast.makeText(getApplicationContext(), "Directory is already exist", Toast.LENGTH_SHORT).show();
        } else {
            file.mkdir();
            if (file.isDirectory()) {
                Toast.makeText(getApplicationContext(), "Directory created", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String message = "Message : failed to create directory" +
                        "\nPath : " + Environment.getExternalStorageDirectory().toString() +
                        "\nmkdirs : " + file.mkdir();
                builder.setMessage(message);
                builder.show();
            }
        }
    }
}
package com.project.safedatastorage;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.safedatastorage.adapter.ViewPagerAdapter;
import com.project.safedatastorage.fragments.FragmentAudio;
import com.project.safedatastorage.fragments.FragmentDocument;
import com.project.safedatastorage.fragments.FragmentImage;
import com.project.safedatastorage.fragments.FragmentVideo;
import com.project.safedatastorage.security.Key;

public class ContainerActivity extends AppCompatActivity {

    private static final String TAG = "ContainerActivity";

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;

    String[] listTitle = {"Фото", "Видео", "Файлы", "Аудио"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        Bundle keyArgument = getIntent().getExtras();
        Key keyObj =  (Key) keyArgument.get("key");

        Log.d(TAG, "onCreate: CREATED KEY");

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);

        adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new FragmentImage(keyObj));
        adapter.addFragment(new FragmentVideo());
        adapter.addFragment(new FragmentDocument());
        adapter.addFragment(new FragmentAudio());

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(
                tabLayout,
                viewPager2,
                (tab, position) -> tab.setText(listTitle[position])
        ).attach();
    }
}
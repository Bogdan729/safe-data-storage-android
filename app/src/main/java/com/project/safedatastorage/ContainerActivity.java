package com.project.safedatastorage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.safedatastorage.adapter.ViewPagerAdapter;
import com.project.safedatastorage.fragments.FragmentFile;
import com.project.safedatastorage.fragments.FragmentImage;
import com.project.safedatastorage.fragments.FragmentVideo;

public class ContainerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;

    String[] listTitle = {"Фото", "Документы", "Видео"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

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
}
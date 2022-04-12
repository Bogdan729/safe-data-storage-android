package com.project.safedatastorage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ImageView imageView;
    Button button;
    Uri imageUri;

    private String[] data = {"Images", "Documents", "Video"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sliding_screen);

        ViewPager2 sampleViewPager = findViewById(R.id.sample_pager);
//        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recycler_internal);
//
//        contacts = Contact.createContactsList(10);
//        ContactsAdapter adapter = new ContactsAdapter(contacts);
//        rvContacts.setAdapter(adapter);
//        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        sampleViewPager.setAdapter(
                new SampleAdapter(this)
        );

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(
                tabLayout,
                sampleViewPager,
                (tab, position) -> tab.setText(data[position])
        ).attach();
    }

    class SampleAdapter extends FragmentStateAdapter {

        public SampleAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public SampleAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public SampleAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new SampleFragment(data[position]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }

    // ВЫБОР ИЗОБРАЖЕНИЯ ИЗ ГАЛЕРЕИ И ЕГО ОТОБРАЖЕНИЕ

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//        imageView = findViewById(R.id.imageView);
//        button = findViewById(R.id.buttonLoadPicture);
//
//        // Вместо переопределения метода onActivityResult используется это выражение
//        // https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
//        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        imageUri = result.getData().getData();
//                        imageView.setImageURI(imageUri);
//                    }
//                });
//
//        button.setOnClickListener((View v) -> {
//                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                someActivityResultLauncher.launch(gallery);
//        });
//    }
}
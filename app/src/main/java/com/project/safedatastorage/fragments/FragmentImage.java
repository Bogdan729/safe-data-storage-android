package com.project.safedatastorage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.ImageViewAdapter;
import com.project.safedatastorage.R;
import com.project.safedatastorage.items.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentImage extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private List<ImageItem> listImages;
    Button addImage;


    public FragmentImage() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_image);

        ImageViewAdapter adapter = new ImageViewAdapter(getContext(), listImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        addImage = view.findViewById(R.id.add_img_btn);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "TEST MSG", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listImages = new ArrayList<>();
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
        listImages.add(new ImageItem("TEST", "14kB", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null)));
    }
}

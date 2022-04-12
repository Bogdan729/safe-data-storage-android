package com.project.safedatastorage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.ImageViewAdapter;
import com.project.safedatastorage.R;
import com.project.safedatastorage.items.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class FragmentImage extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private List<ImageItem> listImages;


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

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listImages = new ArrayList<>();
        listImages.add(new ImageItem("img1"));
        listImages.add(new ImageItem("img2"));
        listImages.add(new ImageItem("img3"));
        listImages.add(new ImageItem("img1"));
        listImages.add(new ImageItem("img2"));
        listImages.add(new ImageItem("img3"));
        listImages.add(new ImageItem("img1"));
        listImages.add(new ImageItem("img2"));
        listImages.add(new ImageItem("img3"));
        listImages.add(new ImageItem("img1"));
        listImages.add(new ImageItem("img2"));
        listImages.add(new ImageItem("img3"));
        listImages.add(new ImageItem("img1"));
        listImages.add(new ImageItem("img2"));
        listImages.add(new ImageItem("img3"));
        listImages.add(new ImageItem("img1"));
        listImages.add(new ImageItem("img2"));
        listImages.add(new ImageItem("img3"));
    }
}

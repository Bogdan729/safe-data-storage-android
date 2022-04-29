package com.project.safedatastorage.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.adapter.ImageViewAdapter;
import com.project.safedatastorage.R;
import com.project.safedatastorage.dao.DataConverter;
import com.project.safedatastorage.items.ImageItem;
import com.project.safedatastorage.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FragmentImage extends Fragment {

    final String TAG = "FragmentImage";

    private List<ImageItem> listImages;
    ImageViewAdapter adapter;

    Button addImage;
    View view;

    Uri imageUri;
    Bitmap bitmap;

    public FragmentImage() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_image);

        adapter = new ImageViewAdapter(getContext(), listImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        addImage = view.findViewById(R.id.add_img_btn);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);


        addImage.setOnClickListener(view -> {
            // Получение доступа ко ГАЛЕРЕИ
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

            // Получение доступа ко ХРАНИЛИЩУ
//            Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
//            gallery.setType("*/*");

            activityResultLauncher.launch(gallery);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_item, null);

        listImages = new ArrayList<>();
        listImages.add(new ImageItem("test", "test", DataConverter.drawableToBitmap(drawable)));
    }

    public void addItem() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_image_item);
        ImageItem imageItem = new ImageItem("TEST2", "143kB", icon);
        listImages.add(imageItem);
        adapter.notifyItemChanged(listImages.size());
    }

    public void saveFile(File file) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Path path = file.toPath();
                @SuppressLint("DefaultLocale")
                String size = String.format("%,d MB", Files.size(path) / (1024 * 1024));
                String fileName = file.getName();

                ImageItem item = new ImageItem(fileName, size, bitmap);
                listImages.add(item);
                adapter.notifyItemChanged(listImages.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            imageUri = result.getData().getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                File itemFile = FileUtil.getFileFromUri(getContext(), imageUri);
                saveFile(itemFile);
                Toast.makeText(getContext(), "file saved", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
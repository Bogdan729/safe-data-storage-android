package com.project.safedatastorage.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.adapter.ImageViewAdapter;
import com.project.safedatastorage.R;
import com.project.safedatastorage.adapter.RVEmptyObserver;
import com.project.safedatastorage.items.ImageItem;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.util.ImageUtil;
import com.project.safedatastorage.writer.FileReaderWriter;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentImage extends Fragment {

    private static final String IMAGE_DIR = Environment.getExternalStorageDirectory().getPath() + "/DataStorage/images";

    private List<ImageItem> listImages;
    private Key keyObj;

    ImageViewAdapter adapter;

    Button addImage;
    View view;

    Uri imageUri;

    public FragmentImage() {

    }

    public FragmentImage(Key keyObj) {
        this.keyObj = keyObj;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_fragment, container, false);
        View emptyView = new View(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_image);
        adapter = new ImageViewAdapter(getContext(), listImages);

        if (listImages == null) {
            RVEmptyObserver observer = new RVEmptyObserver(recyclerView, emptyView);
            adapter.registerAdapterDataObserver(observer);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }

        addImage = view.findViewById(R.id.add_img_btn);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);

        addImage.setOnClickListener(view -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            activityResultLauncher.launch(gallery);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listImages = new ArrayList<>();
        List<File> decryptedImages = FileReaderWriter.readFromInternalStorage(getContext(), keyObj, IMAGE_DIR);

        if (decryptedImages != null) {
            for (File imageFile : decryptedImages) {
                try {
                    Bitmap bitImage = ImageUtil.getThumbnail(imageFile);
                    String name = imageFile.getName();
                    String size = FileUtil.getFormattedFileSize(imageFile.length());

                    int necessaryRotation = FileUtil.getFileExifRotation(imageFile);

                    Bitmap resultImage = ImageUtil.rotateImage(bitImage, necessaryRotation);

                    ImageItem imageItem = new ImageItem(name, size, resultImage);

                    listImages.add(imageItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveFile(Uri uri) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                File file = FileUtil.getFileFromUri(getContext(), uri);

                FileReaderWriter.writeToInternalStorage(file, keyObj, IMAGE_DIR);

                Bitmap bitmap = ImageUtil.getThumbnail(file);
                String size = FileUtil.getFormattedFileSize(file.length());
                String fileName = file.getName();

                int necessaryRotation = FileUtil.getFileExifRotation(file);

                Bitmap result = ImageUtil.rotateImage(bitmap, necessaryRotation);
                ImageItem item = new ImageItem(fileName, size, result);

                listImages.add(item);
                adapter.notifyItemChanged(listImages.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            imageUri = result.getData().getData();
            saveFile(imageUri);

            Toast.makeText(getContext(), "file saved", Toast.LENGTH_LONG).show();
        }
    }

    // ТЕСТРОВЫЕ МЕТОДЫ

}
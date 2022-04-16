package com.project.safedatastorage.fragments;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ImageWriter;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.FileUtil;

import com.project.safedatastorage.ImageViewAdapter;
import com.project.safedatastorage.R;
import com.project.safedatastorage.dao.AppDatabase;
import com.project.safedatastorage.dao.DataConverter;
import com.project.safedatastorage.dao.FileDao;
import com.project.safedatastorage.dao.FileEntity;
import com.project.safedatastorage.items.ImageItem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FragmentImage extends Fragment {

    final String TAG = "FragmentImage";

    FileDao fileDao;

    private List<ImageItem> listImages;
    ImageViewAdapter adapter;

    Button addImage;
    View view;

    Uri imageUri;
    Bitmap bitmap;

    public FragmentImage() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_image);

        adapter = new ImageViewAdapter(getContext(), listImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        addImage = view.findViewById(R.id.add_img_btn);



        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);


        addImage.setOnClickListener(view -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

            someActivityResultLauncher.launch(gallery);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Иницмализация дао объекта
        fileDao = AppDatabase.getInstance(getActivity()).fileDao();

        // !!!!!
        List<FileEntity> fileEntity = fileDao.getAll();
        ImageItem imageItemTEST = new ImageItem(fileEntity.get(3));

        listImages = new ArrayList<>();
        listImages.add(imageItemTEST);
    }

    public void addItem() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_image_item);

        FileEntity fileEntity = fileDao.getFileById(4);
        ImageItem imageItemTEST = new ImageItem(fileEntity);

        // ImageItem imageItem = new ImageItem("TEST2", "143kB", icon);
        listImages.add(imageItemTEST);
        adapter.notifyItemChanged(listImages.size());
    }

    public void saveFile() {
        int size = 123;
        String fileName = "name";
        byte[] fileByteArr = DataConverter.convertImgToBytes(bitmap);

        FileEntity fileEntity = new FileEntity();
        fileEntity.size = size;
        fileEntity.fileName = fileName;
        fileEntity.file = fileByteArr;

        fileDao.insertAll(fileEntity);

        // !!!!!!!!!!!!!!!! добавление элемента в адаптер. Отделить логику
        ImageItem imageItemTEST = new ImageItem(fileEntity);
        listImages.add(imageItemTEST);
        adapter.notifyItemChanged(listImages.size());
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            imageUri = result.getData().getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);

                saveFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(getContext(), "file saved", Toast.LENGTH_LONG).show();
        }
    }
}

package com.project.safedatastorage.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.project.safedatastorage.ImageViewAdapter;
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

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);


        addImage.setOnClickListener(view -> {
            //  Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

//             Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);

            Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
            gallery.setType("*/*");


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
        // int size = 123;
        // String fileName = "name";
//        byte[] fileByteArr = DataConverter.convertImgToBytes(bitmap);

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Path path = file.toPath();
                byte[] fileContent = Files.readAllBytes(path);
                @SuppressLint("DefaultLocale") String size = String.format("%,d MB", Files.size(path) / (1024));
                String fileName = file.getName();

//                fileDao.insertAll(fileEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // !!!!!!!!!!!!!!!! добавление элемента в адаптер. Отделить логику

//        ImageItem imageItemTEST = new ImageItem(fileEntity);
//        listImages.add(imageItemTEST);
//        adapter.notifyItemChanged(listImages.size());
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;

        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            imageUri = result.getData().getData();
            Log.w(TAG, "onActivityResult: " + imageUri);
            try {
                File file = FileUtil.from(getActivity(), imageUri);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    byte[] fileContent = Files.readAllBytes(file.toPath());

                }

                Log.w(TAG, "File...:::: uti - " + file.getPath() + " file -" + file + " : " + file.exists());

            } catch (IOException e) {
                e.printStackTrace();
            }

//            try {
//                // bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
//                 String path = getRealPathFromURI(getContext(), imageUri);
//
//                // Log.w(TAG, "onActivityResult: " + String.format("%,d MB", Files.size(p) / (1024)));
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    Path p = Paths.get(path);
//                    File file = new File(String.valueOf(p));
//
//                    Log.w(TAG, "onActivityResult: " + file.getName());
//                    Log.w(TAG, "onActivityResult: " + String.format("%,d MB", Files.size(p) / (1024)));
//                }
//
//                //saveFile();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            Toast.makeText(getContext(), "file saved", Toast.LENGTH_LONG).show();
        }
    }
}

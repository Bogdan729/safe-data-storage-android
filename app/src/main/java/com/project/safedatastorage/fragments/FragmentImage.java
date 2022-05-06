package com.project.safedatastorage.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.adapter.ImageViewAdapter;
import com.project.safedatastorage.R;
import com.project.safedatastorage.dao.DataConverter;
import com.project.safedatastorage.items.ImageItem;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.util.ImageUtil;
import com.project.safedatastorage.writer.ImageRW;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static android.media.ExifInterface.TAG_ORIENTATION;

public class FragmentImage extends Fragment {

    final String TAG = "FragmentImage";

    private List<ImageItem> listImages;
    private Key key;
    private ImageRW imageRW;

    ImageViewAdapter adapter;

    Button addImage;
    View view;

    File fileItem;
    Uri imageUri;
    Bitmap bitmap;

    public FragmentImage() {}

    public FragmentImage(Key key) {
        this.key = key;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_image);

        imageRW = new ImageRW(this.getContext(), key);

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

    @SuppressLint("DefaultLocale")
    public void saveFile(File file) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            long fileSize = file.length();
            String size = "";

            if (fileSize > 1024 && fileSize < (1024 * 1024)) {
                size = String.format("%,d Kb", fileSize / (1024));
            } else if (fileSize > 1024 * 1024) {
                size = String.format("%,d Mb", fileSize / (1024 * 1024));
            } else {
                size = String.format("%,d byte", fileSize);
            }

            String fileName = file.getName();

            int necessaryRotation = FileUtil.getFileExifRotation(file);

            Matrix matrix = new Matrix();
            matrix.postRotate(necessaryRotation);

            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            ImageItem item = new ImageItem(fileName, size, result);
            listImages.add(item);
            adapter.notifyItemChanged(listImages.size());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            imageUri = result.getData().getData();

            try {

//                bitmap = getThumbnail(imageUri);
//                fileItem = FileUtil.getFileFromUri(getContext(), imageUri);
//                ImageUtil.saveFileToInternalStorage(fileItem);


                // шифрование с сохранение фото в хранилище
//                imageRW.writeToInternalStorage(fileItem);
                
                // получение временных  расшифрованных файлов 
                List<File> filesDec = imageRW.readFromInternalStorage();

                Log.d(TAG, "onActivityResult: " + filesDec.get(0).getName() + "path " + filesDec.get(0).getAbsolutePath());

                if (filesDec.get(0).exists()) {
                    Bitmap bit = DataConverter.convertBytesToImg(Files.readAllBytes(filesDec.get(0).toPath()));
                    System.out.println("HEIGHT " + bit.getHeight());
                }

//                bitmap = ImageUtil.getThumbnail(filesDec.get(0));
                
                // конвертация файла в битмап

//                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/DataStorage/images/dec_20211011_160301.jpg");
//
//                System.out.println("File size : " + Files.size(file.toPath()));
//
//                if (file.exists()) {
//                    Bitmap bit = DataConverter.convertBytesToImg(Files.readAllBytes(file.toPath()));
//
//                    System.out.println("HEIGHT " + bit.getHeight());
//                }

                Toast.makeText(getContext(), "file saved", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ТЕСТРОВЫЕ МЕТОДЫ

}
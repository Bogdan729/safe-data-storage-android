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
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static android.media.ExifInterface.TAG_ORIENTATION;

public class FragmentImage extends Fragment {

    final String TAG = "FragmentImage";
    File internalStorage = new File(Environment.getExternalStorageDirectory().toString());

    private List<ImageItem> listImages;
    private Key key;
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

            int necessaryRotation = getFileExifRotation(file);

            Matrix matrix = new Matrix();
            matrix.postRotate(necessaryRotation);

            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            ImageItem item = new ImageItem(fileName, size, result);
            listImages.add(item);
            adapter.notifyItemChanged(listImages.size());
        }
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            imageUri = result.getData().getData();

            try {

                bitmap = getThumbnail(imageUri);
                fileItem = FileUtil.getFileFromUri(getContext(), imageUri);
                saveFile(fileItem);

                // сохранение на устройство
                 saveFileToInternalStorage();

                Toast.makeText(getContext(), "file saved", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ТЕСТРОВЫЕ МЕТОДЫ

    // работает
    static int getFileExifRotation(File file) {
        ExifInterface exifInterface = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                exifInterface = new ExifInterface(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int orientation = exifInterface.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        switch (orientation) {
            case ORIENTATION_ROTATE_90:
                return 90;
            case ORIENTATION_ROTATE_180:
                return 180;
            case ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    // метод сохранения изображения в хранилище
    public void saveFileToInternalStorage() {
        FileOutputStream fos;
        BufferedOutputStream bos;

        try {
            File file = new File(internalStorage.getPath() + "/DataStorage/images", fileItem.getName());

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);

            FileInputStream fis = new FileInputStream(fileItem);

//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            final int BUFFER = 1024;
            int count;
            byte data[] = new byte[BUFFER];

            while ((count = fis.read(data, 0, BUFFER)) != -1) {
                bos.write(data, 0, count);
            }

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                bos.write(Files.readAllBytes(file.toPath()));
//            }

            bos.flush();
            bos.close();

            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // решает проблему подвисания recycler view
    public Bitmap getThumbnail(Uri uri) throws IOException{
        InputStream input = getContext().getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true; //optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 200) ? (originalSize / 200) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = getContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
}
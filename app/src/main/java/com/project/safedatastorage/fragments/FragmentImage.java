package com.project.safedatastorage.fragments;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.project.safedatastorage.interaction.FileOpener;
import com.project.safedatastorage.interaction.OnFileSelectedListener;
import com.project.safedatastorage.items.ImageItem;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.util.ImageUtil;
import com.project.safedatastorage.writer.FileReaderWriter;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentImage extends Fragment implements OnFileSelectedListener {

    private static final String IMAGE_DIR = Environment.getExternalStorageDirectory().getPath() + "/DataStorage/images";

    private List<ImageItem> listImages;
    private Key keyObj;
    String[] options = {"Rename", "Share", "Delete"};

    ImageViewAdapter adapter;

    Button addImage;
    View view;

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
        adapter = new ImageViewAdapter(getContext(), listImages, this);

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
                    Uri uri = Uri.fromFile(imageFile);
                    String name = imageFile.getName();
                    String size = FileUtil.getFormattedFileSize(imageFile.length());

                    Bitmap bitImage = ImageUtil.getThumbnail(imageFile);
                    int necessaryRotation = FileUtil.getFileExifRotation(imageFile);
                    Bitmap resultImage = ImageUtil.rotateImage(bitImage, necessaryRotation);

                    ImageItem imageItem = new ImageItem(uri, name, size, imageFile, resultImage);

                    listImages.add(imageItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri uri = result.getData().getData();
            ImageItem imgItem = ImageItem.createImage(getContext(), uri);
            FileReaderWriter.writeToInternalStorage(imgItem.getFile(), keyObj, IMAGE_DIR);
            listImages.add(imgItem);
            adapter.notifyItemChanged(listImages.size());
        }
    }

    @Override
    public void onFileClicked(File file) {
        try {
            FileOpener.openFile(getContext(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileLongClicked(File file) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options.");
        ListView options = optionDialog.findViewById(R.id.list_view);
        CustomAdapter customAdapter = new CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return options.length;
        }

        @Override
        public Object getItem(int i) {
            return options[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView =  getLayoutInflater().inflate(R.layout.option_layout, null);
            TextView textOptions = myView.findViewById(R.id.tv_option);
            ImageView imgOptions = myView.findViewById(R.id.iv_option);

            textOptions.setText(options[i]);

            if (options[i].equals("Rename")) {
                imgOptions.setImageResource(R.drawable.ic_rename);
            } else if (options[i].equals("Share")) {
                imgOptions.setImageResource(R.drawable.ic_share);
            } else if (options[i].equals("Delete")) {
                imgOptions.setImageResource(R.drawable.ic_delete);
            }

            return myView;
        }
    }
}
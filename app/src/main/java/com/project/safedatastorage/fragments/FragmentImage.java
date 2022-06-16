package com.project.safedatastorage.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.adapter.CustomAdapter;
import com.project.safedatastorage.adapter.ImageViewAdapter;
import com.project.safedatastorage.R;
import com.project.safedatastorage.adapter.RVEmptyObserver;
import com.project.safedatastorage.interaction.FileOpener;
import com.project.safedatastorage.interaction.OnFileSelectedListener;
import com.project.safedatastorage.items.ImageItem;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.util.ImageUtil;
import com.project.safedatastorage.util.FileReaderWriter;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FragmentImage extends Fragment implements OnFileSelectedListener {

    public static final String TAG = "FragmentImage";

    private static final String IMAGE_DIR = Environment.getExternalStorageDirectory().getPath() + "/DataStorage/images";

    private List<ImageItem> listImages;
    private Key keyObj;

    private final String[] options = {"Переименовать", "Отправить", "Удалить"};

    ImageViewAdapter adapter;
    CustomAdapter customAdapter;

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

        customAdapter = new CustomAdapter(options, this);

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

                    System.out.println("imageItem" + imageItem);

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
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options.");
        ListView listViewOptions = optionDialog.findViewById(R.id.list_view);
        listViewOptions.setAdapter(customAdapter);
        optionDialog.show();

        listViewOptions.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItem = adapterView.getItemAtPosition(i).toString();

            switch (selectedItem) {
                case "Переименовать":
                    AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                    renameDialog.setTitle("Переименовать файл :");
                    final EditText name = new EditText(getContext());
                    renameDialog.setView(name);

                    renameDialog.setPositiveButton("OK", (dialogInterface, i1) -> {
                        ImageItem currentItem = listImages.get(position);

                        String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                        String newName = name.getEditableText().toString() + extension;

                        File currentTemp = new File(file.getAbsolutePath());
                        File destinationTemp = new File(file.getAbsolutePath().replace(currentItem.getName(), newName));

                        if (currentTemp.renameTo(destinationTemp)) {
                            FileUtil.renameFileInInternalStorage(file.getName(), newName, IMAGE_DIR);

                            currentItem.setName(newName);
                            listImages.set(position, currentItem);
                            adapter.notifyItemChanged(position);
                            Toast.makeText(getContext(), "Успешно", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Невозможно переименовать", Toast.LENGTH_SHORT).show();
                        }
                    });

                    renameDialog.setNegativeButton("Отмена", (dialogInterface, i12) -> {
                        optionDialog.cancel();
                    });

                    AlertDialog alertDialogRename = renameDialog.create();
                    alertDialogRename.show();

                    break;

                case "Отправить":
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.setType(URLConnection.guessContentTypeFromName(file.getName()));

                    Uri uri = FileProvider.getUriForFile(getContext(),
                            getContext().getApplicationContext().getPackageName() + ".provider", file);

                    share.putExtra(Intent.EXTRA_STREAM, uri);

                    List<ResolveInfo> resInfoList = getContext().getPackageManager().queryIntentActivities(share, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        getContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    startActivity(Intent.createChooser(share, "Отправить"));
                    break;

                case "Удалить":
                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                    deleteDialog.setTitle("Удалить " + file.getName() + "?");
                    deleteDialog.setPositiveButton("Да", (dialogInterface, i1) -> {
                        FileUtil.deleteFileInInternalStorage(file.getName(), IMAGE_DIR);

                        file.delete();
                        listImages.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Успешно", Toast.LENGTH_SHORT).show();
                    });

                    deleteDialog.setNegativeButton("Нет", (dialogInterface, i2) -> {
                        optionDialog.cancel();
                        Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
                    });

                    AlertDialog alertDialog = deleteDialog.create();
                    alertDialog.show();

                    getLayoutInflater();
                    break;
            }
        });
    }
}
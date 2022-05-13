package com.project.safedatastorage.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.R;
import com.project.safedatastorage.adapter.CustomAdapter;
import com.project.safedatastorage.adapter.DocumentViewAdapter;
import com.project.safedatastorage.adapter.RVEmptyObserver;
import com.project.safedatastorage.interaction.FileOpener;
import com.project.safedatastorage.interaction.OnFileSelectedListener;
import com.project.safedatastorage.items.DocumentItem;
import com.project.safedatastorage.items.VideoItem;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.util.PdfUtil;
import com.project.safedatastorage.writer.FileReaderWriter;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FragmentDocument extends Fragment implements OnFileSelectedListener {

    private static final String FILE_DIR = Environment.getExternalStorageDirectory().getPath() + "/DataStorage/documents";

    private List<DocumentItem> documentsList;
    private Key keyObj;

    private DocumentViewAdapter adapter;
    CustomAdapter customAdapter;

    private Button addDocument;
    private View view;

    private final String[] options = {"Rename", "Share", "Delete"};

    public FragmentDocument(Key keyObj) {
        this.keyObj = keyObj;
    }

    public FragmentDocument() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.document_fragment, container, false);
        View emptyView = new View(getContext());

        customAdapter = new CustomAdapter(options, this);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_document);
        adapter = new DocumentViewAdapter(getContext(), documentsList, this);

        if (documentsList == null) {
            RVEmptyObserver observer = new RVEmptyObserver(recyclerView, emptyView);
            adapter.registerAdapterDataObserver(observer);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }

        addDocument = view.findViewById(R.id.add_document_btn);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);

        addDocument.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            activityResultLauncher.launch(intent);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        documentsList = new ArrayList<>();
        List<File> decryptedVideo = FileReaderWriter.readFromInternalStorage(getContext(), keyObj, FILE_DIR);

        if (decryptedVideo != null) {
            for (File docFile : decryptedVideo) {
                Uri uri = Uri.fromFile(docFile);
                String name = docFile.getName();
                String size = FileUtil.getFormattedFileSize(docFile.length());
                Bitmap thumbnail = PdfUtil.generateThumbnailFromPdf(docFile.getPath());

                DocumentItem videoItem = new DocumentItem(uri, name, size, docFile, thumbnail);
                documentsList.add(videoItem);
            }
        }
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri uri = result.getData().getData();
            DocumentItem documentItem = DocumentItem.createDocument(getContext(), uri);
            FileReaderWriter.writeToInternalStorage(documentItem.getFile(), keyObj, FILE_DIR);
            documentsList.add(documentItem);
            adapter.notifyItemChanged(documentsList.size());
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
                case "Rename":
                    AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                    renameDialog.setTitle("Rename File :");
                    final EditText name = new EditText(getContext());
                    renameDialog.setView(name);

                    renameDialog.setPositiveButton("OK", (dialogInterface, i1) -> {
                        DocumentItem currentItem = documentsList.get(position);

                        String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                        String newName = name.getEditableText().toString() + extension;

                        File currentTemp = new File(file.getAbsolutePath());
                        File destinationTemp = new File(file.getAbsolutePath().replace(currentItem.getName(), newName));

                        if (currentTemp.renameTo(destinationTemp)) {
                            FileUtil.renameFileInInternalStorage(file.getName(), newName, FILE_DIR);

                            currentItem.setName(newName);
                            documentsList.set(position, currentItem);
                            adapter.notifyItemChanged(position);
                            Toast.makeText(getContext(), "Renamed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Couldn't Renamed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    renameDialog.setNegativeButton("Cancel", (dialogInterface, i12) -> {
                        optionDialog.cancel();
                    });

                    AlertDialog alertDialogRename = renameDialog.create();
                    alertDialogRename.show();

                    break;

                case "Share":
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

                    startActivity(Intent.createChooser(share, "Share"));
                    break;

                case "Delete":
                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                    deleteDialog.setTitle("Delete " + file.getName() + "?");
                    deleteDialog.setPositiveButton("Yes", (dialogInterface, i1) -> {
                        FileUtil.deleteFileInInternalStorage(file.getName(), FILE_DIR);

                        file.delete();
                        documentsList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    });

                    deleteDialog.setNegativeButton("No", (dialogInterface, i2) -> {
                        optionDialog.cancel();
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    });

                    AlertDialog alertDialog = deleteDialog.create();
                    alertDialog.show();

                    getLayoutInflater();
                    break;
            }
        });
    }
}
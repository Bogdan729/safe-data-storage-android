package com.project.safedatastorage.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.R;
import com.project.safedatastorage.adapter.DocumentViewAdapter;
import com.project.safedatastorage.adapter.RVEmptyObserver;
import com.project.safedatastorage.adapter.VideoViewAdapter;
import com.project.safedatastorage.items.DocumentItem;
import com.project.safedatastorage.items.VideoItem;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.writer.FileReaderWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentDocument extends Fragment {

    private static final String FILE_DIR = Environment.getExternalStorageDirectory().getPath() + "/DataStorage/documents";

    private List<DocumentItem> documentsList;
    private Key keyObj;

    DocumentViewAdapter adapter;

    Button addDocument;
    View view;

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

        RecyclerView recyclerView = view.findViewById(R.id.recycler_document);
        adapter = new DocumentViewAdapter(getContext(), documentsList);

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
            intent.setType("application/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            activityResultLauncher.launch(intent);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri uri = result.getData().getData();

            DocumentItem documentItem =  DocumentItem.createDocument(getContext(), uri);

            System.out.println(documentItem);
        }
    }
}
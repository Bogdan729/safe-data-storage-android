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
import com.project.safedatastorage.adapter.AudioViewAdapter;
import com.project.safedatastorage.adapter.RVEmptyObserver;
import com.project.safedatastorage.interaction.FileOpener;
import com.project.safedatastorage.interaction.OnFileSelectedListener;
import com.project.safedatastorage.items.AudioItem;
import com.project.safedatastorage.items.VideoItem;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.writer.FileReaderWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentAudio extends Fragment implements OnFileSelectedListener {

    private static final String AUDIO_DIR = Environment.getExternalStorageDirectory().getPath() + "/DataStorage/audio";

    private List<AudioItem> audioList;
    private Key keyObj;

    AudioViewAdapter adapter;

    Button addAudio;
    View view;

    public FragmentAudio(Key keyObj) {
        this.keyObj = keyObj;
    }

    public FragmentAudio() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.audio_fragment, container, false);

        View emptyView = new View(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_audio);
        adapter = new AudioViewAdapter(getContext(), audioList, this);

        if (audioList == null) {
            RVEmptyObserver observer = new RVEmptyObserver(recyclerView, emptyView);
            adapter.registerAdapterDataObserver(observer);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }

        addAudio = view.findViewById(R.id.add_audio_btn);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);

        addAudio.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            activityResultLauncher.launch(intent);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audioList = new ArrayList<>();
        List<File> decryptedAudio = FileReaderWriter.readFromInternalStorage(getContext(), keyObj, AUDIO_DIR);

        if (decryptedAudio != null) {
            for (File audioFile : decryptedAudio) {
                Uri uri = Uri.fromFile(audioFile);
                String name = audioFile.getName();
                String duration = FileUtil.getDurationFromUri(getContext(), uri);
                String size = FileUtil.getFormattedFileSize(audioFile.length());

                AudioItem audioItem = new AudioItem(uri, name, duration, size, audioFile);
                audioList.add(audioItem);
            }
        }
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri uri = result.getData().getData();
            AudioItem audioItem = AudioItem.createAudio(getContext(), uri);
            FileReaderWriter.writeToInternalStorage(audioItem.getFile(), keyObj, AUDIO_DIR);
            audioList.add(audioItem);
            adapter.notifyItemChanged(audioList.size());
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

    }
}
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
import com.project.safedatastorage.adapter.RVEmptyObserver;
import com.project.safedatastorage.adapter.VideoViewAdapter;
import com.project.safedatastorage.items.VideoItem;
import com.project.safedatastorage.security.Key;
import com.project.safedatastorage.util.FileUtil;
import com.project.safedatastorage.writer.FileReaderWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentVideo extends Fragment {

    private static final String VIDEO_DIR = Environment.getExternalStorageDirectory().getPath() + "/DataStorage/video";

    private List<VideoItem> videoList;
    private Key keyObj;

    VideoViewAdapter adapter;

    Button addVideo;
    View view;

    public FragmentVideo(Key keyObj) {
        this.keyObj = keyObj;
    }

    public FragmentVideo() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.video_fragment, container, false);

        View emptyView = new View(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_video);
        adapter = new VideoViewAdapter(getContext(), videoList);

        if (videoList == null) {
            RVEmptyObserver observer = new RVEmptyObserver(recyclerView, emptyView);
            adapter.registerAdapterDataObserver(observer);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }

        addVideo = view.findViewById(R.id.add_video_btn);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);

        addVideo.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            activityResultLauncher.launch(intent);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoList = new ArrayList<>();
        List<File> decryptedVideo = FileReaderWriter.readFromInternalStorage(getContext(), keyObj, VIDEO_DIR);

        if (decryptedVideo != null) {
            for (File videoFile : decryptedVideo) {
                Uri uri = Uri.fromFile(videoFile);
                String name = videoFile.getName();
                String duration = FileUtil.getDurationFromUri(getContext(), uri);
                String size = FileUtil.getFormattedFileSize(videoFile.length());
                Bitmap thumbnail = FileUtil.createVideoThumbnailFromFile(videoFile);

                VideoItem videoItem = new VideoItem(uri, name, duration, size, videoFile, thumbnail);
                videoList.add(videoItem);
            }
        }
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri videoUri = result.getData().getData();
            VideoItem videoItem = VideoItem.createVideo(getContext(), videoUri);
            FileReaderWriter.writeToInternalStorage(videoItem.getFile(), keyObj, VIDEO_DIR);
            videoList.add(videoItem);
            adapter.notifyItemChanged(videoList.size());
        }
    }
}
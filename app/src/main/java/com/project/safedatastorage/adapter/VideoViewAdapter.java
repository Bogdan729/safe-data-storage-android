package com.project.safedatastorage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.R;
import com.project.safedatastorage.interaction.OnFileSelectedListener;
import com.project.safedatastorage.items.VideoItem;

import java.util.List;

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder> {

    private Context context;
    private List<VideoItem> videoItemsList;
    private OnFileSelectedListener listener;

    public VideoViewAdapter(Context context, List<VideoItem> videoItemsList, OnFileSelectedListener listener) {
        this.context = context;
        this.videoItemsList = videoItemsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_and_audio_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewAdapter.ViewHolder holder, int position) {
        holder.videoName.setText(videoItemsList.get(position).getName());
        holder.videoName.setSelected(true);
        holder.videoSize.setText(videoItemsList.get(position).getSize());
        holder.duration.setText(videoItemsList.get(position).getDuration());
        holder.thumbnail.setImageBitmap(videoItemsList.get(position).getThumbnail());

        holder.container.setOnClickListener(view ->
            listener.onFileClicked(videoItemsList.get(position).getFile())
        );

        holder.container.setOnLongClickListener(view -> {
            listener.onFileLongClicked(videoItemsList.get(position).getFile(), position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return videoItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView videoName, videoSize, duration;
        private CardView container;
        private ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.video_and_audio_container);
            videoName = itemView.findViewById(R.id.tv_video_and_audio_name);
            videoSize = itemView.findViewById(R.id.tv_video_and_audio_size);
            duration = itemView.findViewById(R.id.tv_video_and_audio_duration);
            thumbnail = itemView.findViewById(R.id.video_and_audio_thumbnail_view);
        }
    }
}
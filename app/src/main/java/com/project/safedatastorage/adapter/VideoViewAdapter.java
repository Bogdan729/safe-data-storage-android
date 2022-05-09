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
import com.project.safedatastorage.items.VideoItem;

import java.util.List;

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder>{
    Context context;
    List<VideoItem> videoItemsList;

    public VideoViewAdapter(Context context, List<VideoItem> videoItemsList) {
        this.context = context;
        this.videoItemsList = videoItemsList;
    }

    @NonNull
    @Override
    public VideoViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewAdapter.ViewHolder holder, int position) {
        holder.videoName.setText(videoItemsList.get(position).getName());
        holder.videoName.setSelected(true);
        holder.videoSize.setText(videoItemsList.get(position).getSize());
        holder.duration.setText(videoItemsList.get(position).getDuration());
        holder.thumbnail.setImageBitmap(videoItemsList.get(position).getThumbnail());
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
            container = itemView.findViewById(R.id.video_container);
            videoName = itemView.findViewById(R.id.tv_video_name);
            videoSize = itemView.findViewById(R.id.tv_video_size);
            duration = itemView.findViewById(R.id.tv_video_duration);
            thumbnail = itemView.findViewById(R.id.video_thumbnail_view);
        }
    }
}

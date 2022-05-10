package com.project.safedatastorage.adapter;

import android.annotation.SuppressLint;
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
import com.project.safedatastorage.items.AudioItem;

import java.util.List;

public class AudioViewAdapter extends RecyclerView.Adapter<AudioViewAdapter.ViewHolder> {

    Context context;
    List<AudioItem> videoItemsList;

    public AudioViewAdapter(Context context, List<AudioItem> audioItemsList) {
        this.context = context;
        this.videoItemsList = audioItemsList;
    }

    @NonNull
    @Override
    public AudioViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_container, parent, false);
        return new AudioViewAdapter.ViewHolder(view);
    }

    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    public void onBindViewHolder(@NonNull AudioViewAdapter.ViewHolder holder, int position) {
        holder.audioName.setText(videoItemsList.get(position).getName());
        holder.audioName.setSelected(true);
        holder.audioSize.setText(videoItemsList.get(position).getSize());
        holder.duration.setText(videoItemsList.get(position).getDuration());
        holder.thumbnail.setImageDrawable(context.getDrawable(R.drawable.ic_audio));
    }

    @Override
    public int getItemCount() {
        return videoItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView audioName, audioSize, duration;
        private CardView container;
        private ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.video_container);
            audioName = itemView.findViewById(R.id.tv_video_name);
            audioSize = itemView.findViewById(R.id.tv_video_size);
            duration = itemView.findViewById(R.id.tv_video_duration);
            thumbnail = itemView.findViewById(R.id.video_thumbnail_view);
        }
    }
}
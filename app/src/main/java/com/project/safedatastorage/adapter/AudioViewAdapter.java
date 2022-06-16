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
import com.project.safedatastorage.interaction.OnFileSelectedListener;
import com.project.safedatastorage.items.AudioItem;

import java.util.List;

public class AudioViewAdapter extends RecyclerView.Adapter<AudioViewAdapter.ViewHolder> {

    private Context context;
    private List<AudioItem> videoItemsList;
    private OnFileSelectedListener listener;

    public AudioViewAdapter(Context context, List<AudioItem> audioItemsList, OnFileSelectedListener listener) {
        this.context = context;
        this.videoItemsList = audioItemsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AudioViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_and_audio_container, parent, false);
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
        private TextView audioName, audioSize, duration;
        private CardView container;
        private ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.video_and_audio_container);
            audioName = itemView.findViewById(R.id.tv_video_and_audio_name);
            audioSize = itemView.findViewById(R.id.tv_video_and_audio_size);
            duration = itemView.findViewById(R.id.tv_video_and_audio_duration);
            thumbnail = itemView.findViewById(R.id.video_and_audio_thumbnail_view);
        }
    }
}
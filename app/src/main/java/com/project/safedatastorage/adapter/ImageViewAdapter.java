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
import com.project.safedatastorage.items.ImageItem;

import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ViewHolder> {

    private Context context;
    private List<ImageItem> imageItemsList;
    private OnFileSelectedListener listener;

    public ImageViewAdapter(Context context, List<ImageItem> imageItemsList, OnFileSelectedListener listener) {
        this.context = context;
        this.imageItemsList = imageItemsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_and_doc_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imgName.setText(imageItemsList.get(position).getName());
        holder.imgName.setSelected(true);
        holder.imgSize.setText(imageItemsList.get(position).getSize());
        holder.imgView.setImageBitmap(imageItemsList.get(position).getThumbnail());

        holder.container.setOnClickListener(view ->
                listener.onFileClicked(imageItemsList.get(position).getFile())
        );

        holder.container.setOnLongClickListener(view -> {
            listener.onFileLongClicked(imageItemsList.get(position).getFile());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return imageItemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView imgName, imgSize;
        private CardView container;
        private ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.image_and_doc_container);
            imgName = itemView.findViewById(R.id.tv_name);
            imgSize = itemView.findViewById(R.id.tv_size);
            imgView = itemView.findViewById(R.id.img_view);
        }
    }
}
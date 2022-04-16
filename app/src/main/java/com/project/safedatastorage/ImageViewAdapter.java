package com.project.safedatastorage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.safedatastorage.items.ImageItem;

import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ViewHolder> {

    Context context;
    List<ImageItem> imageItemList;

    public ImageViewAdapter(Context context, List<ImageItem> imageItemList) {
        this.context = context;
        this.imageItemList = imageItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imgName.setText(imageItemList.get(position).getImageName());
        holder.imgSize.setText(imageItemList.get(position).getImageSize());
//        holder.imgView.setImageBitmap();
        holder.imgView.setImageBitmap(imageItemList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return imageItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView imgName, imgSize;
        private CardView container;
        private ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.image_container);
            imgName = itemView.findViewById(R.id.tv_img_name);
            imgSize = itemView.findViewById(R.id.tv_img_size);
            imgView = itemView.findViewById(R.id.photo_img_view);
        }
    }
}
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
import com.project.safedatastorage.items.DocumentItem;

import java.util.List;

public class DocumentViewAdapter extends RecyclerView.Adapter<DocumentViewAdapter.ViewHolder> {

    private Context context;
    private List<DocumentItem> documentItemList;
    private OnFileSelectedListener listener;

    public DocumentViewAdapter(Context context, List<DocumentItem> documentItemList, OnFileSelectedListener listener) {
        this.context = context;
        this.documentItemList = documentItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_and_doc_container, parent, false);
        return new DocumentViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewAdapter.ViewHolder holder, int position) {
        holder.docName.setText(documentItemList.get(position).getName());
        holder.docName.setSelected(true);
        holder.docSize.setText(documentItemList.get(position).getSize());
        holder.thumbnail.setImageBitmap(documentItemList.get(position).getThumbnail());

        holder.container.setOnClickListener(view ->
                listener.onFileClicked(documentItemList.get(position).getFile())
        );

        holder.container.setOnLongClickListener(view -> {
            listener.onFileLongClicked(documentItemList.get(position).getFile());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return documentItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView docName, docSize;
        private CardView container;
        private ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.image_and_doc_container);
            docName = itemView.findViewById(R.id.tv_name);
            docSize = itemView.findViewById(R.id.tv_size);
            thumbnail = itemView.findViewById(R.id.img_view);
        }
    }
}
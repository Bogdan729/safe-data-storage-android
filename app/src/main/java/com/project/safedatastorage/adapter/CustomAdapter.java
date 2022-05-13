package com.project.safedatastorage.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.project.safedatastorage.R;

public class CustomAdapter extends BaseAdapter {

    private final String[] options;
    private final Fragment fragment;

    public CustomAdapter(String[] options, Fragment fragment) {
        this.options = options;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public Object getItem(int i) {
        return options[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View myView = fragment.getLayoutInflater().inflate(R.layout.option_layout, null);
        TextView textOptions = myView.findViewById(R.id.tv_option);
        ImageView imgOptions = myView.findViewById(R.id.iv_option);

        textOptions.setText(options[i]);

        if (options[i].equals("Rename")) {
            imgOptions.setImageResource(R.drawable.ic_rename);
        } else if (options[i].equals("Share")) {
            imgOptions.setImageResource(R.drawable.ic_share);
        } else if (options[i].equals("Delete")) {
            imgOptions.setImageResource(R.drawable.ic_delete);
        }

        return myView;
    }
}
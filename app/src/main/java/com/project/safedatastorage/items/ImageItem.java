package com.project.safedatastorage.items;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageItem {
    private String imageName;
    private String imageSize;
    private Bitmap image;
    private ImageView imageView;

    public ImageItem(String imageName, String imageSize, Bitmap image) {
        this.imageName = imageName;
        this.imageSize = imageSize;
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
package com.project.safedatastorage.items;

import android.graphics.drawable.Drawable;

public class ImageItem {
    private String imageName;
    private String imageSize;
    private Drawable image;

    public ImageItem(String imageName, String imageSize, Drawable image) {
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

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}

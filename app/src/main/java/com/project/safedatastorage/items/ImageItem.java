package com.project.safedatastorage.items;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.project.safedatastorage.dao.DataConverter;
import com.project.safedatastorage.dao.FileEntity;

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

    public ImageItem(FileEntity entity) {
        this.imageName = entity.getFileName();
        this.imageSize = String.valueOf(entity.getSize()); // !!!!!!!!!!!!!!!!!!
        this.image = DataConverter.convertBytesToImg(entity.getFile()); // !!!!!!!!!!!!!!!!!!
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

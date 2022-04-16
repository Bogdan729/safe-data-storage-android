package com.project.safedatastorage.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "files")
public class FileEntity {
    @PrimaryKey(autoGenerate = true)
    public int fid;

    @ColumnInfo(name = "file_name")
    public String fileName;

    @ColumnInfo(name = "size", typeAffinity = ColumnInfo.INTEGER)
    public int size;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] file;

    public int getFid() {
        return fid;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSize() {
        return size;
    }

    public byte[] getFile() {
        return file;
    }
}

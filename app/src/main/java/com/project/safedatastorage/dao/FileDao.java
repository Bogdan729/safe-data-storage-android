package com.project.safedatastorage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FileDao {
    @Query("SELECT * FROM files")
    List<FileEntity> getAll();

    @Query("SELECT * FROM files WHERE fid =:fileId")
    FileEntity getFileById(int fileId);

    @Insert
    void insertAll(FileEntity... files);

    @Update
    void updateFiles(FileEntity file);

    @Delete
    void delete(FileEntity file);
}
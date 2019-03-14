package com.example.android.moviematch.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieRepoDao {
    @Insert
    void insert(MovieRepo repo);

    @Delete
    void delete(MovieRepo repo);

    @Query("SELECT * FROM MovieRepo")
    LiveData<List<MovieRepo>> getAllRepos();

    @Query("SELECT * FROM MovieRepo WHERE full_name = :fullName LIMIT 1")
    LiveData<MovieRepo> getRepoByName(String fullName);
}

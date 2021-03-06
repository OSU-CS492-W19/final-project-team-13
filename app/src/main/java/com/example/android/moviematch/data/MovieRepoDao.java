package com.example.android.moviematch.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieRepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieRepo repo);

    @Delete
    void delete(MovieRepo repo);

    @Query("SELECT * FROM savedmovies WHERE saved = 1")
    LiveData<List<MovieRepo>> getAllRepos();

    @Query("SELECT * FROM savedmovies WHERE id = :id LIMIT 1")
    LiveData<MovieRepo> getRepoByName(int id);

}

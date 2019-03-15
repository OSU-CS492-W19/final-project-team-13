package com.example.android.moviematch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "SavedMovies")
public class MovieRepo implements Serializable {
    @NonNull
    @PrimaryKey
    public int id;

    public String poster_path;
    public String overview;
    public String release_date;
    public String original_title;
    public String original_language;
    public String title;
    public Double popularity;
    public Double vote_average;
}
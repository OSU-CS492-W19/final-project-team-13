package com.example.android.moviematch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "MovieRepo")
public class MovieRepo implements Serializable {
    @NonNull
    @PrimaryKey
    public String full_name;

    public String description;
    public String html_url;
    public int stargazers_count;
}

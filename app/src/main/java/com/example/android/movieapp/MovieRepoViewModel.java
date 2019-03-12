package com.example.android.movieapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.android.movieapp.data.MovieRepo;
import com.example.android.movieapp.data.MovieRepoRepository;

import java.util.List;

public class MovieRepoViewModel extends AndroidViewModel {
    private MovieRepoRepository mMovieRepoRepository;

    public MovieRepoViewModel(Application application) {
        super(application);
        mMovieRepoRepository = new MovieRepoRepository(application);
    }

    public void insertMovieRepo(MovieRepo repo) {
        mMovieRepoRepository.insertMovieRepo(repo);
    }

    public void deleteMovieRepo(MovieRepo repo) {
        mMovieRepoRepository.deleteMovieRepo(repo);
    }

    public LiveData<List<MovieRepo>> getAllMovieRepos() {
        return mMovieRepoRepository.getAllMovieRepos();
    }

    public LiveData<MovieRepo> getMovieRepoByName(String fullName) {
        return mMovieRepoRepository.getMovieRepoByName(fullName);
    }
}

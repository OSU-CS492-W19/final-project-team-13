package com.example.android.moviematch.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MovieRepoRepository {
    private MovieRepoDao mMovieRepoDao;

    public MovieRepoRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mMovieRepoDao = db.movieRepoDao();
    }

    public void insertMovieRepo(MovieRepo repo) {
        new InsertAsyncTask(mMovieRepoDao).execute(repo);
    }

    public void deleteMovieRepo(MovieRepo repo) {
        new DeleteAsyncTask(mMovieRepoDao).execute(repo);
    }

    public LiveData<List<MovieRepo>> getAllMovieRepos() {
        return mMovieRepoDao.getAllRepos();
    }

    public LiveData<MovieRepo> getMovieRepoByName(String fullName) {
        return mMovieRepoDao.getRepoByName(fullName);
    }

    private static class InsertAsyncTask extends AsyncTask<MovieRepo, Void, Void> {
        private MovieRepoDao mAsyncTaskDao;
        InsertAsyncTask(MovieRepoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MovieRepo... movieRepos) {
            mAsyncTaskDao.insert(movieRepos[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<MovieRepo, Void, Void> {
        private MovieRepoDao mAsyncTaskDao;
        DeleteAsyncTask(MovieRepoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MovieRepo... movieRepos) {
            mAsyncTaskDao.delete(movieRepos[0]);
            return null;
        }
    }
}

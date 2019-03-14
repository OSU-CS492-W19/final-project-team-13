package com.example.android.moviematch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.moviematch.data.MovieRepo;
import com.example.android.moviematch.utils.MovieDiscoverUtils;

import java.util.List;

public class SavedMoviesActivity extends AppCompatActivity implements MovieSearchAdapter.OnSearchItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search_results);

        RecyclerView savedReposRV = findViewById(R.id.rv_saved_repos);
        savedReposRV.setLayoutManager(new LinearLayoutManager(this));
        savedReposRV.setHasFixedSize(true);

        final MovieSearchAdapter adapter = new MovieSearchAdapter(this);
        savedReposRV.setAdapter(adapter);

        MovieRepoViewModel viewModel = ViewModelProviders.of(this).get(MovieRepoViewModel.class);
        viewModel.getAllMovieRepos().observe(this, new Observer<List<MovieRepo>>() {
            @Override
            public void onChanged(@Nullable List<MovieRepo> movieRepos) {
                adapter.updateSearchResults(movieRepos);
            }
        });
    }

    @Override
    public void onSearchItemClick(MovieRepo repo) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDiscoverUtils.EXTRA_MOVIE_REPO, repo);
        startActivity(intent);
    }
}

package com.example.android.moviematch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.example.android.moviematch.data.MovieRepo;
import com.example.android.moviematch.utils.MovieUtils;

import java.util.List;

public class SavedMoviesActivity extends AppCompatActivity implements MovieSearchAdapter.OnSearchItemClickListener, NavigationView.OnNavigationItemSelectedListener  {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search_results);

        mDrawerLayout = findViewById(R.id.drawer_layout_saved);

        NavigationView navigationView = findViewById(R.id.nv_nav_drawer_saved);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.saved_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu);

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
        intent.putExtra(MovieUtils.EXTRA_MOVIE_REPO, repo);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                return true;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.nav_search:
                Intent savedReposIntent = new Intent(this, MovieSearchActivity.class);
                startActivity(savedReposIntent);
                return true;
            default:
                return false;
        }
    }
}

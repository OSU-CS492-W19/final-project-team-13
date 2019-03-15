package com.example.android.moviematch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.moviematch.data.MovieRepo;
import com.example.android.moviematch.utils.MovieUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String REPOS_ARRAY_KEY = "movieRepos";
    private static final String SEARCH_URL_KEY = "movieSearchURL";

    private static final int MOVIE_SEARCH_LOADER_ID = 0;

    private TextView mLoadingErrorTV;
    private ProgressBar mLoadingPB;
    private DrawerLayout mDrawerLayout;
    private ImageView mImageView;

    private TextView mTitle;
    private TextView mRating;

    private MovieUtils.MovieSearchResults mResults;
    private ArrayList<MovieRepo> mRepos;

    private int NumberOfPages = 50;
    private int RandomYear;
    private int RandomPage;
    private int RandomMovie;
    private Calendar cal;

    private boolean GrabPageNum = true;

    private String sort;
    private String filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingErrorTV = findViewById(R.id.tv_loading_error);
        mLoadingPB = findViewById(R.id.pb_loading);
        mDrawerLayout = findViewById(R.id.drawer_layout_main);

        mTitle = findViewById(R.id.tv_title);
        mRating = findViewById(R.id.tv_rating);
        mImageView = findViewById(R.id.poster_img);

        NavigationView navigationView = findViewById(R.id.nv_nav_drawer_main);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu);

        if (savedInstanceState != null && savedInstanceState.containsKey(REPOS_ARRAY_KEY)) {
            mRepos = (ArrayList<MovieRepo>) savedInstanceState.getSerializable(REPOS_ARRAY_KEY);
        }

        getSupportLoaderManager().initLoader(MOVIE_SEARCH_LOADER_ID, null, this);

        //Get current year for upper bound
        Date today = new Date();
        cal = Calendar.getInstance();
        cal.setTime(today);

        Button searchButton = findViewById(R.id.btn_refresh);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRandomMovie();
            }
        });

        getRandomMovie();
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

    private void getRandomMovie() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sort = ""; // = preferences.getString(getString(R.string.pref_sort_key),
                //getString(R.string.pref_sort_default));
        filter = ""; // = preferences.getString(getString(R.string.pref_language_key),
                //getString(R.string.pref_language_default));

        RandomYear = randomGenerator(1950, cal.get(Calendar.YEAR));
        RandomMovie = randomGenerator(0, 20);

        String url = MovieUtils.buildMovieDiscoverURL(sort, RandomYear, filter);

        Log.d("RandomYear", Integer.toString(RandomYear));
        Log.d("RandomMovie", Integer.toString(RandomMovie));

        Log.d("Half URL", url);

        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, url);
        mLoadingPB.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(MOVIE_SEARCH_LOADER_ID, args, this);
    }

    private int randomGenerator(int lower, int upper){
        Random r = new Random();
        return r.nextInt(upper-lower) + lower;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRepos != null) {
            outState.putSerializable(REPOS_ARRAY_KEY, mRepos);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        String url = null;
        if (bundle != null) {
            url = bundle.getString(SEARCH_URL_KEY);
        }
        return new MovieSearchLoader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        Log.d(TAG, "loader finished loading");

        if(GrabPageNum){
            GrabPageNum = false;
            Log.d("GrabPageNum", "Setting to false");

            if (s != null) {
                mResults = MovieUtils.parseMovieResults(s);
                NumberOfPages = mResults.total_pages;
                Log.d("NumberOfPages", Integer.toString(NumberOfPages));

                RandomPage = randomGenerator(0, NumberOfPages);
                Log.d("RandomPage", Integer.toString(RandomPage));


                if(RandomPage > 1000){
                    RandomPage = randomGenerator(0, 1000);
                    Log.d("RandomPage", Integer.toString(RandomPage));
                }

                String url = MovieUtils.buildMovieDiscoverURL(sort, RandomYear, RandomPage, filter);
                Log.d("Full URL", url);


                Bundle args = new Bundle();
                args.putString(SEARCH_URL_KEY, url);
                getSupportLoaderManager().restartLoader(MOVIE_SEARCH_LOADER_ID, args, this);
            } else {
                mLoadingErrorTV.setVisibility(View.VISIBLE);
            }
            mLoadingPB.setVisibility(View.INVISIBLE);
        } else {
            GrabPageNum = true;
            Log.d("GrabPageNum", "Re-setting to true");

            if (s != null) {
                mLoadingErrorTV.setVisibility(View.INVISIBLE);
                mResults = MovieUtils.parseMovieResults(s);
                mRepos = mResults.results;

                Log.d("Results", "Results returned");

                mTitle.setText(mRepos.get(RandomMovie).title);
                mRating.setText(String.valueOf(mRepos.get(RandomMovie).vote_average));

                Log.d("Title:Rating", mRepos.get(RandomMovie).title + ":" + mRepos.get(RandomMovie).vote_average);

                String iconURL = MovieUtils.buildMoviePosterURL(300, mRepos.get(RandomMovie).poster_path);

                Log.d("iconURL", iconURL);

                Glide.with(this).load(iconURL).into(mImageView);
            } else {
                mLoadingErrorTV.setVisibility(View.VISIBLE);
            }
            mLoadingPB.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        // Nothing to do here...
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_search:
                Intent movieSearchIntent = new Intent(this, MovieSearchActivity.class);
                startActivity(movieSearchIntent);
                return true;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.nav_saved:
                Intent savedReposIntent = new Intent(this, SavedMoviesActivity.class);
                startActivity(savedReposIntent);
                return true;
            default:
                return false;
        }
    }
}
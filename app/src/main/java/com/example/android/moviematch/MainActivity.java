package com.example.android.moviematch;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.android.moviematch.data.MovieRepo;
import com.example.android.moviematch.utils.MovieUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MOVIE_KEY = "currentMovie";
    private static final String SEARCH_URL_KEY = "movieSearchURL";
    private static final String RAND_MOVIE_KEY = "MovieID";

    private static final int MOVIE_SEARCH_LOADER_ID = 0;

    private TextView mLoadingErrorTV;
    private ProgressBar mLoadingPB;
    private DrawerLayout mDrawerLayout;
    private ImageView mImageView;
    private ImageView mImagePoster;
    private TextView mImageText;
    private View mView;

    private TextView mTitle;
    private TextView mRating;
    private TextView mOverview;
    private TextView mExtra;

    private MovieRepoViewModel mMovieRepoViewModel;

    private MovieUtils.MovieSearchResults mResults;
    private ArrayList<MovieRepo> mMovieList;
    private MovieRepo mMovie;

    private int NumberOfPages = 50;
    private int RandomYear;
    private int RandomPage;
    private int RandomMovie;
    private Calendar cal;

    public static boolean GrabPageNum = true;
    public static boolean ChangedOrient = false;

    private String sort;
    private String filter;
    private String vote_count;
    private String avg_rating;
    private String release_date;
    private String genre;

    private GestureDetector mDetector;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingErrorTV = findViewById(R.id.tv_loading_error);
        mLoadingPB = findViewById(R.id.pb_loading);
        mDrawerLayout = findViewById(R.id.drawer_layout_main);

        mTitle = findViewById(R.id.tv_title);
        mRating = findViewById(R.id.tv_rating);
        mOverview = findViewById(R.id.tv_overview);
        mExtra = findViewById(R.id.tv_extra);
        mImageView = findViewById(R.id.poster_img);
        mImagePoster = findViewById(R.id.poster_background);
        mImageText = findViewById(R.id.No_Image);

        mMovie = null;

        mView = findViewById(R.id.main_view);
        mDetector = new GestureDetector(this, new MyGestureListener());
        mView.setOnTouchListener(touchListener);

        NavigationView navigationView = findViewById(R.id.nv_nav_drawer_main);
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mMovieRepoViewModel = ViewModelProviders.of(this).get(MovieRepoViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu);

        context = getApplicationContext();

        getSupportLoaderManager().initLoader(MOVIE_SEARCH_LOADER_ID, null, this);

        //Get current year for upper bound
        Date today = new Date();
        cal = Calendar.getInstance();
        cal.setTime(today);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_KEY)) {
            GrabPageNum = false;
            mMovie = (MovieRepo) savedInstanceState.getSerializable(MOVIE_KEY);
            RandomMovie = (int) savedInstanceState.getSerializable(RAND_MOVIE_KEY);
            assignValues();
        } else {
            getRandomMovie();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_repo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            case R.id.action_share:
                shareRepo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getRandomMovie() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sort = preferences.getString(getString(R.string.pref_sort_key),
                                    getString(R.string.pref_sort_default));
        filter = ""; // = preferences.getString(getString(R.string.pref_language_key),
                //getString(R.string.pref_language_default));
        vote_count = preferences.getString(getString(R.string.pref_vote_count_key), "");
        avg_rating = preferences.getString(getString(R.string.pref_vote_avg_key), "");
        release_date = preferences.getString(getString(R.string.pref_release_year_key), "0");
        if (release_date.equals("")){
            release_date = "0";
        }
        genre = preferences.getString(getString(R.string.pref_genre_key), getString(R.string.pref_genre_default));

        //default is set to 1950
        RandomYear = randomGenerator(Integer.parseInt(release_date), cal.get(Calendar.YEAR));
        RandomMovie = randomGenerator(0, 20);

        String url = MovieUtils.buildMovieDiscoverURL(sort, RandomYear, vote_count, avg_rating, genre);

        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, url);
        mLoadingPB.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(MOVIE_SEARCH_LOADER_ID, args, this);
    }

    private int randomGenerator(int lower, int upper){
        Random r = new Random();
        //error: Does not work if lower is same as upper
        if (upper - lower <= 0){
            lower = upper - 1;
        }
        Log.d(TAG, "randomGenerator: " + r.nextInt(upper-lower) + lower);
        return r.nextInt(upper-lower) + lower;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovie != null) {
            outState.putSerializable(MOVIE_KEY, mMovie);
            outState.putSerializable(RAND_MOVIE_KEY, RandomMovie);
            ChangedOrient = false;
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

        if(GrabPageNum && !ChangedOrient){
            GrabPageNum = false;
            if (s != null) {
                createFullURL(s);
            } else {
                mLoadingErrorTV.setVisibility(View.VISIBLE);
                mLoadingPB.setVisibility(View.INVISIBLE);
            }
        } else {
            ChangedOrient = true;
            GrabPageNum = true;
            if (s != null) {
                mLoadingErrorTV.setVisibility(View.INVISIBLE);
                mResults = MovieUtils.parseMovieResults(s);
                mMovieList = mResults.results;
                mMovie = mMovieList.get(RandomMovie);
                Log.d(TAG, "onLoadFinished: GrabPageNum is false, now we here" + mMovie.title);
                assignValues();
            } else {
                mLoadingErrorTV.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.INVISIBLE);
                mImageText.setVisibility(View.VISIBLE);
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

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mDetector.onTouchEvent(event);
        }
    };

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private int MIN_SWIPE_DISTANCE_X = 300;
        private int MAX_SWIPE_DISTANCE_X = 1000;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaX = e1.getX() - e2.getX();
            float deltaXAbs = Math.abs(deltaX);
            if((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X))
            {
                if(deltaX > 0)
                {
                    CharSequence text = "Movie added to saved list";
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    mMovie.saved=true;
                    mMovieRepoViewModel.insertMovieRepo(mMovie);
                    getRandomMovie();
                } else {
                    mMovie.saved=false;
                    mMovieRepoViewModel.insertMovieRepo(mMovie);
                    getRandomMovie();
                }
            }
            return true;
        }
    }

    public void createFullURL(String s){
        mResults = MovieUtils.parseMovieResults(s);
        NumberOfPages = mResults.total_pages;
        RandomPage = randomGenerator(1, NumberOfPages);

        if(RandomPage > 1000){
            RandomPage = randomGenerator(1, 1000);
        }

        String url = MovieUtils.buildMovieDiscoverURL(sort, RandomYear, RandomPage, vote_count, avg_rating, genre);
        Log.d("Full URL", url);

        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, url);
        getSupportLoaderManager().restartLoader(MOVIE_SEARCH_LOADER_ID, args, this);
    }

    public void assignValues(){
        String rating = "Rating: " + String.valueOf(mMovie.vote_average) + "/10     Votes: " + String.valueOf(mMovie.vote_count);
        String extra = "Release Date: " + mMovie.release_date + "     Language: " + mMovie.original_language;
        mTitle.setText(mMovie.title);
        mRating.setText(rating);
        mOverview.setText(mMovie.overview);
        mExtra.setText(extra);

        if(mMovie.poster_path != null && mMovie.backdrop_path != null) {
            String posterURL = MovieUtils.buildMoviePosterURL(mMovie.backdrop_path);
            String iconURL = MovieUtils.buildMoviePosterURL(300, mMovie.poster_path);

            Log.d("iconURL", iconURL);
            Log.d("posterURL", posterURL);

            mImageView.setVisibility(View.VISIBLE);
            mImagePoster.setVisibility(View.VISIBLE);
            mImageText.setVisibility(View.INVISIBLE);

            Glide.with(this).load(posterURL).transition(DrawableTransitionOptions.withCrossFade()).into(mImagePoster);
            Glide.with(this).load(iconURL).transition(DrawableTransitionOptions.withCrossFade()).into(mImageView);
        } else if(mMovie.poster_path != null){
            String iconURL = MovieUtils.buildMoviePosterURL(300, mMovie.poster_path);

            Log.d("iconURL", iconURL);

            mImageView.setVisibility(View.VISIBLE);
            mImagePoster.setVisibility(View.INVISIBLE);
            mImageText.setVisibility(View.INVISIBLE);
            Glide.with(this).load(iconURL).transition(DrawableTransitionOptions.withCrossFade()).into(mImageView);
        } else {
                mImageView.setVisibility(View.INVISIBLE);
                mImagePoster.setVisibility(View.INVISIBLE);
                mImageText.setVisibility(View.VISIBLE);
        }
    }

    public void shareRepo() {
        if (mMovie != null) {
            String shareText = getString(R.string.share_repo_text, mMovie.title, mMovie.overview);
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(shareText)
                    .setChooserTitle(R.string.share_chooser_title)
                    .startChooser();
        }
    }
}
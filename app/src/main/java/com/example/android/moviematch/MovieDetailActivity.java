package com.example.android.moviematch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.android.moviematch.data.MovieRepo;
import com.example.android.moviematch.utils.MovieUtils;

public class MovieDetailActivity extends AppCompatActivity {

    //private TextView mLoadingErrorTV;
    //private ProgressBar mLoadingPB;
    //private DrawerLayout mDrawerLayout;
    private ImageView mImageView;
    private ImageView mImagePoster;
    private TextView mImageText;
    private View mView;

    private TextView mTitle;
    private TextView mRating;
    private TextView mOverview;
    private TextView mExtra;


    private MovieRepoViewModel mMovieRepoViewModel;
    private MovieRepo mRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_detail);

        mTitle = findViewById(R.id.tv_title);
        mRating = findViewById(R.id.tv_rating);
        mOverview = findViewById(R.id.tv_overview);
        mExtra = findViewById(R.id.tv_extra);
        mImageView = findViewById(R.id.poster_img);
        mImagePoster = findViewById(R.id.poster_background);
        mImageText = findViewById(R.id.No_Image);

        mMovieRepoViewModel = ViewModelProviders.of(this).get(MovieRepoViewModel.class);

        mRepo = null;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MovieUtils.EXTRA_MOVIE_REPO)) {
            mRepo = (MovieRepo) intent.getSerializableExtra(MovieUtils.EXTRA_MOVIE_REPO);
            String rating = "Rating: " + String.valueOf(mRepo.vote_average) + "/10     Votes: " + String.valueOf(mRepo.vote_count);
            String extra = "Release Date: " + mRepo.release_date + "     Language: " + mRepo.original_language;
            mTitle.setText(mRepo.title);
            mRating.setText(rating);
            mOverview.setText(mRepo.overview);
            mExtra.setText(extra);

            if(mRepo.poster_path != null && mRepo.backdrop_path != null) {
                String posterURL = MovieUtils.buildMoviePosterURL(mRepo.backdrop_path);
                String iconURL = MovieUtils.buildMoviePosterURL(300, mRepo.poster_path);

                Log.d("iconURL", iconURL);
                Log.d("posterURL", posterURL);

                mImageView.setVisibility(View.VISIBLE);
                mImagePoster.setVisibility(View.VISIBLE);
                mImageText.setVisibility(View.INVISIBLE);

                Glide.with(this).load(posterURL).transition(DrawableTransitionOptions.withCrossFade()).into(mImagePoster);
                Glide.with(this).load(iconURL).transition(DrawableTransitionOptions.withCrossFade()).into(mImageView);
            } else if(mRepo.poster_path != null){
                String iconURL = MovieUtils.buildMoviePosterURL(300, mRepo.poster_path);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareRepo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareRepo() {
        if (mRepo != null) {
            String shareText = getString(R.string.share_repo_text, mRepo.title, mRepo.overview);
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(shareText)
                    .setChooserTitle(R.string.share_chooser_title)
                    .startChooser();
        }
    }
}

package com.example.android.moviematch.utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.moviematch.BuildConfig;
import com.example.android.moviematch.data.MovieRepo;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MovieUtils {
    private final static String MOVIE_SEARCH_BASE_URL = "https://api.themoviedb.org/3/search/movie";
    private final static String MOVIE_DISCOVER_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private final static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p";

    private final static String MOVIE_SEARCH_QUERY_PARAM = "query";
    private final static String MOVIE_DISCOVER_SORT_PARAM = "sort_by";
    private final static String MOVIE_DISCOVER_RD_PARAM = "primary_release_date.gte";
    private final static String MOVIE_DISCOVER_PAGE_PARAM = "page";
    private final static String MOVIE_DISCOVER_FILTER_RELEASE_YEAR_PARAM = "primary_release_year";
    private final static String MOVIE_DISCOVER_VOTE_COUNT_PARAM = "vote_count.gte";
    private final static String MOVIE_DISCOVER_AVG_RATING_PARAM = "vote_average.gte";
    private final static String MOVIE_DISCOVER_GENRE_PARAM = "with_genres";
    private final static String MOVIE_POSTER_WIDTH_PARAM = "w";

    private final static String MOVIE_API_PARAM = "api_key";
    private final static String MOVIE_API_KEY = "a3b25b48aba236d99a5b03f61bab59ea";
    public static final String EXTRA_MOVIE_REPO = "MovieUtils.MovieRepo";

    public static class MovieSearchResults {
        public ArrayList<MovieRepo> results;
        public int total_pages;
    }

    public static String buildMovieDiscoverURL(String sort, int releaseYear, int pageNum, String voteCount, String avgRating, String genre){
        Uri.Builder builder = Uri.parse(MOVIE_DISCOVER_BASE_URL).buildUpon();
        builder.appendQueryParameter(MOVIE_API_PARAM, MOVIE_API_KEY);

        if (!sort.equals("")) {
            builder.appendQueryParameter(MOVIE_DISCOVER_SORT_PARAM, sort);
        }
        if (!voteCount.equals("")) {
            builder.appendQueryParameter(MOVIE_DISCOVER_VOTE_COUNT_PARAM, voteCount);
        }
        if (!avgRating.equals("")){
            builder.appendQueryParameter(MOVIE_DISCOVER_AVG_RATING_PARAM, avgRating);
        }
        if (!genre.equals("")){
            builder.appendQueryParameter(MOVIE_DISCOVER_GENRE_PARAM, genre);
        }
        builder.appendQueryParameter(MOVIE_DISCOVER_RD_PARAM, Integer.toString(releaseYear));
        builder.appendQueryParameter(MOVIE_DISCOVER_PAGE_PARAM, Integer.toString(pageNum));
        return builder.build().toString();
    }

    public static String buildMovieDiscoverURL(String sort, int releaseYear, String voteCount, String avgRating, String genre) {

        Uri.Builder builder = Uri.parse(MOVIE_DISCOVER_BASE_URL).buildUpon();
        builder.appendQueryParameter(MOVIE_API_PARAM, MOVIE_API_KEY);

        if (!sort.equals("")) {
            builder.appendQueryParameter(MOVIE_DISCOVER_SORT_PARAM, sort);
        }
        if (!voteCount.equals("")) {
            builder.appendQueryParameter(MOVIE_DISCOVER_VOTE_COUNT_PARAM, voteCount);
        }
        if (!avgRating.equals("")){
            builder.appendQueryParameter(MOVIE_DISCOVER_AVG_RATING_PARAM, avgRating);
        }
        if (!genre.equals("")){
            builder.appendQueryParameter(MOVIE_DISCOVER_GENRE_PARAM, genre);
        }
        builder.appendQueryParameter(MOVIE_DISCOVER_RD_PARAM, Integer.toString(releaseYear));
        return builder.build().toString();
    }


    //
    // URL Parsing Stuff for Search Activity
    //

    public static String buildMovieSearchURL(String query) {
        return Uri.parse(MOVIE_SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter(MOVIE_API_PARAM, MOVIE_API_KEY)
                .appendQueryParameter(MOVIE_SEARCH_QUERY_PARAM, query)
                .build()
                .toString();
    }

    public static MovieSearchResults parseMovieResults(String json) {
        Gson gson = new Gson();
        MovieSearchResults results = gson.fromJson(json, MovieSearchResults.class);
        if (results != null && results != null) {
            return results;
        } else {
            return null;
        }
    }


    //
    // URL Parsing Stuff for Posters
    //

    public static String buildMoviePosterURL(String path) {
        Log.d("POSTER PATH", path);
        return Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath("original")
                .appendEncodedPath(path)
                .build()
                .toString();
    }

    public static String buildMoviePosterURL(int size, String path) {
        String sizeS = Integer.toString(size);
        Uri.Builder builder = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon();

        if (!sizeS.equals("")) {
            builder.appendPath(MOVIE_POSTER_WIDTH_PARAM + size);
        } else {
            builder.appendPath("original");
        }
        builder.appendEncodedPath(path);

        return builder.build().toString();
    }
}

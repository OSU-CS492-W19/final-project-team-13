package com.example.android.moviematch.utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.android.moviematch.data.MovieRepo;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MovieUtils {
    private final static String MOVIE_SEARCH_BASE_URL = "https://api.themoviedb.org/3/search/movie";
    private final static String MOVIE_DISCOVER_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private final static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    private final static String MOVIE_SEARCH_QUERY_PARAM = "query";
    private final static String MOVIE_DISCOVER_SORT_PARAM = "sort_by";
    private final static String MOVIE_DISCOVER_FILTER_RELEASE_YEAR_PARAM = "primary_release_year";
    private final static String MOVIE_POSTER_WIDTH_PARAM = "w";

    private final static String MOVIE_API_PARAM = "api_key";
    private final static String MOVIE_API_KEY = "a3b25b48aba236d99a5b03f61bab59ea";
    public static final String EXTRA_MOVIE_REPO = "MovieUtils.MovieRepo";

    public static class MovieSearchResults {
        public ArrayList<MovieRepo> items;
    }

    public static String buildMovieDiscoverURL() {
        return Uri.parse(MOVIE_DISCOVER_BASE_URL).buildUpon()
                .appendQueryParameter(MOVIE_API_PARAM, MOVIE_API_KEY)
                .build()
                .toString();
    }

    public static String buildMovieDiscoverURL(String sort, String filter) {

        Uri.Builder builder = Uri.parse(MOVIE_DISCOVER_BASE_URL).buildUpon();

        /*
         * Finally append the query parameters into the URL, including sort only if specified.
         */
            builder.appendQueryParameter(MOVIE_API_PARAM, MOVIE_API_KEY);

        if (!sort.equals("")) {
            builder.appendQueryParameter(MOVIE_DISCOVER_SORT_PARAM, sort);
        }

        if (!filter.equals("")) {
            builder.appendQueryParameter(MOVIE_DISCOVER_FILTER_RELEASE_YEAR_PARAM, filter);
        }

        return builder.build().toString();
    }

    public static ArrayList<MovieRepo> parseMovieDiscoverResults(String json) {
        Gson gson = new Gson();
        MovieSearchResults results = gson.fromJson(json, MovieSearchResults.class);
        if (results != null && results.items != null) {
            return results.items;
        } else {
            return null;
        }
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

    public static ArrayList<MovieRepo> parseMovieSearchResults(String json) {
        Gson gson = new Gson();
        MovieSearchResults results = gson.fromJson(json, MovieSearchResults.class);
        if (results != null && results.items != null) {
            return results.items;
        } else {
            return null;
        }
    }


    //
    // URL Parsing Stuff for Posters
    //


    public static String buildMoviePosterURL(String path) {
        return Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath("original")
                .appendPath(path)
                .build()
                .toString();
    }

    public static String buildMoviePosterURL(String size, String path) {

        Uri.Builder builder = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon();

        /*
         * Finally append the query parameters into the URL, including sort only if specified.
         */
        if (!size.equals("")) {
            builder.appendPath(MOVIE_POSTER_WIDTH_PARAM + size);
        } else {
            builder.appendPath("original");
        }

        builder.appendPath(path);

        return builder.build().toString();
    }

}

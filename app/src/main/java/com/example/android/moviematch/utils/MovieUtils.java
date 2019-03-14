package com.example.android.moviematch.utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.android.moviematch.data.MovieRepo;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MovieUtils {
    private final static String MOVIE_SEARCH_BASE_URL = "https://api.github.com/search/repositories";
    private final static String MOVIE_SEARCH_QUERY_PARAM = "q";
    private final static String MOVIE_SEARCH_SORT_PARAM = "sort";
    private final static String MOVIE_SEARCH_SORT_VALUE = "stars";
    private final static String MOVIE_SEARCH_LANGUAGE_FORMAT_STR = "language:%s";
    private final static String MOVIE_SEARCH_USER_FORMAT_STR = "user:%s";
    private final static String MOVIE_SEARCH_SEARCH_IN_FORMAT_STR = "in:%s";
    private final static String MOVIE_SEARCH_IN_NAME = "name";
    private final static String MOVIE_SEARCH_IN_DESCRIPTION = "description";
    private final static String MOVIE_SEARCH_IN_README = "readme";

    public static final String EXTRA_MOVIE_REPO = "MovieUtils.MovieRepo";

    public static class MovieSearchResults {
        public ArrayList<MovieRepo> items;
    }

    public static String buildMovieSearchURL(String query) {
        return Uri.parse(MOVIE_SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter(MOVIE_SEARCH_QUERY_PARAM, query)
                .appendQueryParameter(MOVIE_SEARCH_SORT_PARAM, MOVIE_SEARCH_SORT_VALUE)
                .build()
                .toString();
    }

    public static String buildMovieSearchURL(String query, String sort, String language,
                                             String user, boolean searchInName,
                                             boolean searchInDescription, boolean searchInReadme) {

        Uri.Builder builder = Uri.parse(MOVIE_SEARCH_BASE_URL).buildUpon();

        /*
         * Language, username, and search-in terms are incorporated directly into the query
         * parameter, e.g. "q=android language:java user:square".  Below, we simply fold those
         * terms into the query parameter if they're specified.
         */
        if (!language.equals("")) {
            query += " " + String.format(MOVIE_SEARCH_LANGUAGE_FORMAT_STR, language);
        }

        if (!user.equals("")) {
            query += " " + String.format(MOVIE_SEARCH_USER_FORMAT_STR, user);
        }

        String searchIn = buildSearchInURLString(searchInName, searchInDescription, searchInReadme);
        if (searchIn != null) {
            query += " " + String.format(MOVIE_SEARCH_SEARCH_IN_FORMAT_STR, searchIn);
        }

        /*
         * Finally append the query parameters into the URL, including sort only if specified.
         */
        builder.appendQueryParameter(MOVIE_SEARCH_QUERY_PARAM, query);
        if (!sort.equals("")) {
            builder.appendQueryParameter(MOVIE_SEARCH_SORT_PARAM, sort);
        }

        return builder.build().toString();
    }

    @Nullable
    private static String buildSearchInURLString(boolean searchInName, boolean searchInDescription,
                                                 boolean searchInReadme) {
        ArrayList<String> searchInTerms = new ArrayList<>();
        if (searchInName) {
            searchInTerms.add(MOVIE_SEARCH_IN_NAME);
        }
        if (searchInDescription) {
            searchInTerms.add(MOVIE_SEARCH_IN_DESCRIPTION);
        }
        if (searchInReadme) {
            searchInTerms.add(MOVIE_SEARCH_IN_README);
        }

        if (!searchInTerms.isEmpty()) {
            return TextUtils.join(",", searchInTerms);
        } else {
            return null;
        }
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
}

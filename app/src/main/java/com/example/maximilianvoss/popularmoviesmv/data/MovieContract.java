package com.example.maximilianvoss.popularmoviesmv.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ruedigervoss on 05/04/16.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.maximilianvoss.popularmoviesmv";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIEDB_ID = "movie_db_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULAR = "popular";
        public static final String COLUMN_TOP_RATED = "top_rated";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_IMPORT_DATE = "date";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPopularMovies() {
            String popularity = "popular";
            return CONTENT_URI.buildUpon().appendPath(popularity).build();
        }

        public static Uri buildTopRatedMovies() {
            String top_rated = "top_rated";
            return CONTENT_URI.buildUpon().appendPath(top_rated).build();
        }

        public static Uri buildFavoriteMovies() {
            String favorite = "favorite";
            return CONTENT_URI.buildUpon().appendPath(favorite).build();
        }

        public static String getMDBIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}

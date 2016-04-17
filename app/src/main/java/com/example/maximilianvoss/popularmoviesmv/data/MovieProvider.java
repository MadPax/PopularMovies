package com.example.maximilianvoss.popularmoviesmv.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by ruedigervoss on 05/04/16.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;


    static final int MOVIES = 100;
    static final int MOVIE_BY_MDB_ID = 101;
    static final int MOVIE_POPULAR = 102;
    static final int MOVIE_TOP_RATED = 103;
    static final int MOVIE_FAVORITE = 104;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;

    static{

        sMovieQueryBuilder = new SQLiteQueryBuilder();

        sMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME
        );
    }

    private static final String sMovieByMovieIDSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_MOVIEDB_ID + " = ? ";

    private static final String sPopularMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_POPULAR + " = ? ";

    private static final String sTopRatedMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_TOP_RATED + " = ? ";

    private static final String sFavoriteMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_FAVORITE + " = ? ";

    private Cursor getMovieByMovieDBId(Uri uri, String[] projection, String sortOrder){
        String mdbId = MovieContract.MovieEntry.getMDBIdFromUri(uri);
        String selection = sMovieByMovieIDSelection;
        String[] selectionArgs = new String[] { mdbId };

        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getPopularMovies(String[] projection, String sortOrder){

            String categorySelection = sPopularMovieSelection;
            String[] popularMoviesSelectionArgs = new String[]{"1"};

            return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    categorySelection,
                    popularMoviesSelectionArgs,
                    null,
                    null,
                    sortOrder);
    }

    private Cursor getTopRatedMovies(String[] projection, String sortOrder){

        String categorySelection = sTopRatedMovieSelection;
        String[] topRatedMoviesSelectionArgs = new String[]{"1"};

        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                categorySelection,
                topRatedMoviesSelectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getFavoriteMovies(String[] projection, String sortOrder){

        String categorySelection = sFavoriteMovieSelection;
        String[] favoriteMoviesSelectionArgs = new String[]{"1"};

        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                categorySelection,
                favoriteMoviesSelectionArgs,
                null,
                null,
                sortOrder);
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        final String popularString = "popular";
        final String topRatedString = "top_rated";
        final String favoriteString = "favorite";

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_BY_MDB_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + popularString, MOVIE_POPULAR);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + topRatedString, MOVIE_TOP_RATED);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + favoriteString, MOVIE_FAVORITE);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_MDB_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_POPULAR:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_TOP_RATED:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_FAVORITE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){

        Cursor retCursor;
        switch (sUriMatcher.match(uri)){

            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );
                break;
            }

            case MOVIE_BY_MDB_ID: {
                retCursor = getMovieByMovieDBId(uri, projection, sortOrder);
                break;
            }

            case MOVIE_POPULAR: {
                retCursor = getPopularMovies(projection, sortOrder);
                break;
            }

            case MOVIE_TOP_RATED: {
                retCursor = getTopRatedMovies(projection, sortOrder);
                break;
            }

            case MOVIE_FAVORITE: {
                retCursor = getFavoriteMovies(projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case MOVIES: {

                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {

            case MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {

            case MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        try {
                            long _id = db.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        } catch (SQLiteException e){
                            String selection = sMovieByMovieIDSelection;
                            String[] selectionArgs = new String[]{ value.getAsString(MovieContract.MovieEntry.COLUMN_MOVIEDB_ID) };
                            db.update(MovieContract.MovieEntry.TABLE_NAME, value, selection, selectionArgs);
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}

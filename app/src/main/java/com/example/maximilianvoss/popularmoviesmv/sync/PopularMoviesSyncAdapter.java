package com.example.maximilianvoss.popularmoviesmv.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.example.maximilianvoss.popularmoviesmv.BuildConfig;
import com.example.maximilianvoss.popularmoviesmv.R;
import com.example.maximilianvoss.popularmoviesmv.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by ruedigervoss on 17/04/16.
 */
public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public PopularMoviesSyncAdapter (Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        String POPULAR_MOVIES_QUERY_PARAM = "popular";
        String TOP_RATED_MOVIES_QUERY_PARAM = "top_rated";

        String[] queryParams = new String[]{POPULAR_MOVIES_QUERY_PARAM, TOP_RATED_MOVIES_QUERY_PARAM};

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String MovieJsonStr = null;


        final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie";
        final String APIKEY_PARAM = "api_key";

        for (String param : queryParams) {

            try {

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(param)
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                MovieJsonStr = buffer.toString();
                getMovieDatafromJSON(MovieJsonStr, param);
            } catch (IOException e) {

                Log.e(LOG_TAG, "Error ", e);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

        }

        return;
    }

    private void getMovieDatafromJSON(String jsonMovies, String sortOrder)
            throws JSONException {

        final String MDB_RESULTS = "results";
        final String MDB_MOVIE_ID = "id";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_MOVIE_NAME = "title";
        final String MDB_USER_RATING = "vote_average";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";

        final String AbsolutePath = "http://image.tmdb.org/t/p/w342/";

        try {

            JSONObject JsonMovies = new JSONObject(jsonMovies);
            JSONArray movieArray = JsonMovies.getJSONArray(MDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String PopularMovie;
            String TopRatedMovie;

            String POPULAR_TAG = "popular";
            String TOP_RATED_TAG = "top_rated";

            if (sortOrder.equals(TOP_RATED_TAG)){
                PopularMovie = null;
                TopRatedMovie = "1";

            } else {
                PopularMovie = "1";
                TopRatedMovie = null;
            }

            for (int i = 0; i < movieArray.length(); i++) {

                long dateTime;
                String MoviePosterPath;
                String MovieDB_ID;
                String MovieName;
                String UserRating;
                String Overview;
                String ReleaseDate;


                JSONObject MovieJSON = movieArray.getJSONObject(i);

                dateTime = dayTime.setJulianDay(julianStartDay + i);

                MoviePosterPath = AbsolutePath + MovieJSON.getString(MDB_POSTER_PATH);
                MovieDB_ID = MovieJSON.getString(MDB_MOVIE_ID);
                MovieName = MovieJSON.getString(MDB_MOVIE_NAME);
                UserRating = MovieJSON.getString(MDB_USER_RATING);
                Overview = MovieJSON.getString(MDB_OVERVIEW);
                ReleaseDate = MovieJSON.getString(MDB_RELEASE_DATE);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, MoviePosterPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIEDB_ID, MovieDB_ID);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, MovieName);
                movieValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, UserRating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, Overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, ReleaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_IMPORT_DATE, dateTime);

                if (PopularMovie != null) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, PopularMovie);
                }
                if (TopRatedMovie != null) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_TOP_RATED, TopRatedMovie);
                }

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

                getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_IMPORT_DATE + " <= ?",
                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopularMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}

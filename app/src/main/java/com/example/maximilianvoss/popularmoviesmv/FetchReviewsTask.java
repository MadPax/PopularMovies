package com.example.maximilianvoss.popularmoviesmv;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ruedigervoss on 08/04/16.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private ReviewAdapter mReviewAdapter;
    private ArrayList<Review> mArrayList = new ArrayList<>();
    private Context mContext;

    public FetchReviewsTask(Context context, ReviewAdapter adapter) {
        mContext = context;
        mReviewAdapter = adapter;
    }

    protected ArrayList<Review> doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String ReviewJsonStr = null;

        Log.v(LOG_TAG, "param is: " + params[0]);

        final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie";
        final String APIKEY_PARAM = "api_key";
        final String QUERY_PARAM = params[0];
        final String REVIEWS_PARAM = "reviews";

        try {

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendPath(QUERY_PARAM)
                    .appendPath(REVIEWS_PARAM)
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
                return null;
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
                return null;
            }
            ReviewJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
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

        try {
            return getTrailerDatafromJSON(ReviewJsonStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(ArrayList<Review> result) {
        if (result != null) {
            mArrayList = result;
            mReviewAdapter.addAll(mArrayList);
            mReviewAdapter.notifyDataSetChanged();
            // New data is back from the server.  Hooray!
        }
    }

    private ArrayList<Review> getTrailerDatafromJSON(String jsonReviews)
            throws JSONException {

        ArrayList<Review> returnList = new ArrayList<>();

        final String MDB_RESULTS = "results";
        final String MDB_REVIEW_AUTHOR = "author";
        final String MDB_REVIEW_CONTENT = "content";

        JSONObject JsonReviews = new JSONObject(jsonReviews);
        JSONArray ReviewArray = JsonReviews.getJSONArray(MDB_RESULTS);

        for (int i = 0; i < ReviewArray.length(); i++) {

            String ReviewAuthor;
            String ReviewContent;


            JSONObject ReviewJSON = ReviewArray.getJSONObject(i);

            ReviewAuthor = ReviewJSON.getString(MDB_REVIEW_AUTHOR);
            ReviewContent = ReviewJSON.getString(MDB_REVIEW_CONTENT);


            Review newReview = new Review(ReviewAuthor, ReviewContent);

            returnList.add(newReview);
        }

        return returnList;
    }
}

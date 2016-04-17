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
 * Created by ruedigervoss on 07/04/16.
 */
public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<Trailer>> {

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    private TrailerAdapter mTrailerAdapter;
    private ArrayList<Trailer> mArrayList = new ArrayList<>();
    private Context mContext;

    public FetchTrailerTask(Context context, TrailerAdapter adapter) {
        mContext = context;
        mTrailerAdapter = adapter;
    }

    protected ArrayList<Trailer> doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String TrailerJsonStr = null;

        final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie";
        final String APIKEY_PARAM = "api_key";
        final String QUERY_PARAM = params[0];
        final String TRAILER_PARAM = "videos";

        try {

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendPath(QUERY_PARAM)
                    .appendPath(TRAILER_PARAM)
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
            TrailerJsonStr = buffer.toString();

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
            Log.v(LOG_TAG, TrailerJsonStr);
            return getTrailerDatafromJSON(TrailerJsonStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(ArrayList<Trailer> result) {
        if (result != null) {
            mArrayList = result;
            Log.v(LOG_TAG, "Size of the ArrayList is " + String.valueOf(mArrayList.size()));
            mTrailerAdapter.addAll(mArrayList);
            Log.v(LOG_TAG, "Size of the TrailerAdapter is " + String.valueOf(mTrailerAdapter.getCount()));
            mTrailerAdapter.notifyDataSetChanged();
            // New data is back from the server.  Hooray!
        }
    }

    private ArrayList<Trailer> getTrailerDatafromJSON(String jsonTrailers)
            throws JSONException {

        ArrayList<Trailer> returnList = new ArrayList<>();

        final String MDB_RESULTS = "results";
        final String MDB_TRAILER_KEY = "key";
        final String MDB_TRAILER_NAME = "name";

        final String VIDEO_YOUTUBE_BASE_PATH = "http://www.youtube.com/watch?v=";

        JSONObject JsonTrailers = new JSONObject(jsonTrailers);
        JSONArray TrailerArray = JsonTrailers.getJSONArray(MDB_RESULTS);

        for (int i = 0; i < TrailerArray.length(); i++) {

            String TrailerName;
            String TrailerPath;


            JSONObject TrailerJSON = TrailerArray.getJSONObject(i);

            TrailerName = TrailerJSON.getString(MDB_TRAILER_NAME);
            TrailerPath = VIDEO_YOUTUBE_BASE_PATH + TrailerJSON.getString(MDB_TRAILER_KEY);


            Trailer newTrailer = new Trailer(TrailerName, TrailerPath);

            returnList.add(newTrailer);
        }

        return returnList;
    }
}

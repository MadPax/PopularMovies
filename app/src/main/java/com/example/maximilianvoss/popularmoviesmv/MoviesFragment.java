package com.example.maximilianvoss.popularmoviesmv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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
 * Created by ruedigervoss on 17/03/16.
 */
public class MoviesFragment extends Fragment {

    private MoviesAdapter mMoviesAdapter;
    private GridView mGridView;
    private ArrayList<Movie> movieArrayList = new ArrayList<>();

    private ArrayAdapter<String> mForecastAdapter;

    public MoviesFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        FetchMoviesTask movieTask = new FetchMoviesTask();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = sharedPref.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_default));
        movieTask.execute(sort_order);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());

        mGridView = (GridView) rootView.findViewById(R.id.movies_gridview);
        mGridView.setAdapter(mMoviesAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie SelectedMovie = (Movie)mMoviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", SelectedMovie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private ArrayList<Movie> getMovieDatafromJSON(String jsonMovies)
            throws JSONException {

            ArrayList<Movie> returnList = new ArrayList<Movie>();

            final String MDB_RESULTS = "results";
            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_MOVIE_NAME = "title";
            final String MDB_USER_RATING = "vote_average";
            final String MDB_OVERVIEW = "overview";
            final String MDB_RELEASE_DATE = "release_date";

            final String AbsolutePath = "http://image.tmdb.org/t/p/w185/";

            JSONObject JsonMovies = new JSONObject(jsonMovies);
            JSONArray MovieArray = JsonMovies.getJSONArray(MDB_RESULTS);

            for (int i = 0; i < MovieArray.length(); i++){

                String MoviePosterPath;
                String MovieName;
                String UserRating;
                String Overview;
                String ReleaseDate;


                JSONObject MovieJSON = MovieArray.getJSONObject(i);

                MoviePosterPath = AbsolutePath + MovieJSON.getString(MDB_POSTER_PATH);
                MovieName = MovieJSON.getString(MDB_MOVIE_NAME);
                UserRating = MovieJSON.getString(MDB_USER_RATING);
                Overview = MovieJSON.getString(MDB_OVERVIEW);
                ReleaseDate = MovieJSON.getString(MDB_RELEASE_DATE);


                Movie newMovie = new Movie(MoviePosterPath, MovieName, UserRating, Overview, ReleaseDate);

                returnList.add(newMovie);
            }

            return returnList;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        protected ArrayList<Movie> doInBackground(String...params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String MovieJsonStr = null;

            Log.v(LOG_TAG, "param is: " + params[0]);

            final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie";
            final String APIKEY_PARAM = "api_key";
            final String QUERY_PARAM = params[0];

            try {

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(QUERY_PARAM)
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
                MovieJsonStr = buffer.toString();

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

                return getMovieDatafromJSON(MovieJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                Log.v("OnPostExecute", "We got until OnPostExecute");
                movieArrayList = result;
                mMoviesAdapter.addAll(movieArrayList);
                mMoviesAdapter.notifyDataSetChanged();
                // New data is back from the server.  Hooray!
            }
        }
    }
}

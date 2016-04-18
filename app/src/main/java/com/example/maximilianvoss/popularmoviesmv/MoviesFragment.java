package com.example.maximilianvoss.popularmoviesmv;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.Spinner;

import com.example.maximilianvoss.popularmoviesmv.data.MovieContract;
import com.example.maximilianvoss.popularmoviesmv.sync.PopularMoviesSyncAdapter;

/**
 * Created by ruedigervoss on 17/03/16.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = MoviesFragment.class.getSimpleName();

    private static final int MOVIE_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";
    private static final String SELECTED_SORT_ORDER = "selected_sort_order";
    private static final String DEFAULT_SORT_ORDER = "popular";


    private static final String[] FORECAST_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIEDB_ID,
            MovieContract.MovieEntry.COLUMN_POPULAR,
            MovieContract.MovieEntry.COLUMN_TOP_RATED
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_POSTER_PATH = 1;
    static final int COL_MOVIE_MDB_ID = 2;
    static final int COL_MOVIE_POPULAR = 3;
    static final int COL_MOVIE_TOP_RATED = 4;

    private int mPosition = GridView.INVALID_POSITION;
    private MoviesAdapter mMoviesAdapter;
    private GridView mGridView;
    private String mSortOrder;

    private String POPULAR_SEARCH_KEY = "popular";
    private String TOP_RATED_SEARCH_KEY = "top_rated";
    private String FAVORITE_SEARCH_KEY = "favorite";

    public MoviesFragment(){

    }

    public interface Callback {

        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mSortOrder = savedInstanceState.getString(SELECTED_SORT_ORDER);
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        } else {
            mSortOrder = DEFAULT_SORT_ORDER;
        }
        Log.v(LOG_TAG, "In onCreate: " + mSortOrder);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.android_action_bar_spinner_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_order_array, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);

        int CurrentlySelectedItem;

        if (mSortOrder.equals(TOP_RATED_SEARCH_KEY)) CurrentlySelectedItem = 1;
        else if (mSortOrder.equals(FAVORITE_SEARCH_KEY)) CurrentlySelectedItem = 2;
        else CurrentlySelectedItem = 0;

        spinner.setSelection(CurrentlySelectedItem);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Object item = parent.getSelectedItem().toString();

                if (item.equals("Popular")) onSortOrderChanged(POPULAR_SEARCH_KEY);
                if (item.equals("Top Rated")) onSortOrderChanged(TOP_RATED_SEARCH_KEY);
                if (item.equals("Favorite")) onSortOrderChanged(FAVORITE_SEARCH_KEY);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);

        mGridView = (GridView) rootView.findViewById(R.id.movies_gridview);
        mGridView.setAdapter(mMoviesAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            ((Callback) getActivity())
                .onItemSelected(MovieContract.MovieEntry.buildMovieUri(
                        cursor.getInt(COL_MOVIE_MDB_ID)
                ));

            mPosition = position;
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri sortedMovieQuery;

        Log.v(LOG_TAG, "In onCreateLoader: " + mSortOrder);

        if (mSortOrder.equals("top_rated")) {
            sortedMovieQuery = MovieContract.MovieEntry.buildTopRatedMovies();
        } else if (mSortOrder.equals("popular")) {
            sortedMovieQuery = MovieContract.MovieEntry.buildPopularMovies();
        } else {
            sortedMovieQuery = MovieContract.MovieEntry.buildFavoriteMovies();
        }

        return new CursorLoader(getActivity(),
            sortedMovieQuery,
            FORECAST_COLUMNS,
            null,
            null,
            null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        outState.putString(SELECTED_SORT_ORDER, mSortOrder);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        mMoviesAdapter.swapCursor(cursor);

        if (mPosition != GridView.INVALID_POSITION) {

            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMoviesAdapter.swapCursor(null);
    }

    private void updateMovies(String sort_order) {
        PopularMoviesSyncAdapter.syncImmediately(getActivity());
    }

    void onSortOrderChanged(String sort_order) {
        mSortOrder = sort_order;
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        Log.v(LOG_TAG, "In onSortOrderChanged: " + mSortOrder);
    }

}

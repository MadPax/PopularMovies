package com.example.maximilianvoss.popularmoviesmv;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.maximilianvoss.popularmoviesmv.data.MovieContract;
import com.example.maximilianvoss.popularmoviesmv.sync.PopularMoviesSyncAdapter;

/**
 * Created by ruedigervoss on 17/03/16.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER = 0;

    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

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

    private MoviesAdapter mMoviesAdapter;
    private GridView mGridView;
    private String mSortOrder = "popular";

    public MoviesFragment(){

    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        String initial_sort_order = "popular";
        super.onStart();
        updateMovies(initial_sort_order);
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
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMoviesAdapter.swapCursor(cursor);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
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
        if (sort_order.equals("popular") || sort_order.equals("top_rated")) {
            updateMovies(sort_order);
        }
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

}

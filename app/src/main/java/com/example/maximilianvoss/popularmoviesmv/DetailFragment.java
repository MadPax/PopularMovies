package com.example.maximilianvoss.popularmoviesmv;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.example.maximilianvoss.popularmoviesmv.data.MovieContract;
import com.example.maximilianvoss.popularmoviesmv.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ruedigervoss on 07/04/16.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #PopularMoviesApp";
    static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 0;

    private Uri mUri;
    private String mMdbId;
    private ShareActionProvider mShareActionProvider;

    private TrailerAdapter mTrailerAdapter;
    private ListView mTrailerListView;

    private ReviewAdapter mReviewAdapter;
    private ListView mReviewListView;

    private static final String[] DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_MOVIEDB_ID,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_FAVORITE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_USER_RATING,
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIEDB_ID = 2;
    public static final int COL_POSTER_PATH = 3;
    public static final int COL_FAVORITE = 4;
    public static final int COL_OVERVIEW = 5;
    public static final int COL_RELEASE_DATE = 6;
    public static final int COL_USER_RATING = 7;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mReleaseYearView;
    private RatingBar mRatingView;
    private TextView mOverviewView;

    private String mFavorite;
    private CheckBox mFavoriteCheckbox;

    private String CHECKBOX_CHECKED = "1";
    private String CHECKBOX_UNCHECKED = "0";

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialization block for the Movie Data

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            mMdbId = MovieEntry.getMDBIdFromUri(mUri);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mImageView = (ImageView) rootView.findViewById(R.id.imageView);
        mTitleView = (TextView) rootView.findViewById(R.id.titleView);
        mReleaseYearView = (TextView) rootView.findViewById(R.id.releaseView);
        mRatingView = (RatingBar) rootView.findViewById(R.id.popularityView);
        mOverviewView = (TextView) rootView.findViewById(R.id.overviewView);
        mFavoriteCheckbox = (CheckBox) rootView.findViewById(R.id.favorite_checkbox);

        mFavoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            ContentValues value = new ContentValues();
            String selection = MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIEDB_ID + " = ?";
            String[] selectionArgs = new String[]{ mMdbId };

            if ( isChecked ){
                value.put(MovieEntry.COLUMN_FAVORITE, CHECKBOX_CHECKED);
            } else if ( !isChecked ){
                value.put(MovieEntry.COLUMN_FAVORITE, CHECKBOX_UNCHECKED);
            }

            getActivity().getContentResolver().update(MovieEntry.CONTENT_URI, value, selection, selectionArgs);
            }
        });

        //Initialization block for the Trailer Layout

        mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
        mTrailerListView = (ListView)rootView.findViewById(R.id.trailer_listview);
        TextView trailerEmptyView = (TextView)rootView.findViewById(R.id.trailerEmptyView);
        mTrailerListView.setAdapter(mTrailerAdapter);
        mTrailerListView.setEmptyView(trailerEmptyView);

        mTrailerListView.setOnItemClickListener( new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            Trailer trailer = (Trailer) mTrailerAdapter.getItem(position);
            String trailerPath = trailer.getPath();
            startActivity(
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse(trailerPath)
                ));
            }
        });

        //Initialization block for the Review Layout

        mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Trailer>());
        mReviewListView = (ListView)rootView.findViewById(R.id.review_listview);
        TextView reviewEmptyView = (TextView)rootView.findViewById(R.id.reviewEmptyView);
        mReviewListView.setAdapter(mReviewAdapter);
        mReviewListView.setEmptyView(reviewEmptyView);

        //Return rootView
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

       if (null != mTrailerAdapter && !mTrailerAdapter.isEmpty()) {
           mShareActionProvider.setShareIntent(createShareForecastIntent());
       }

    }

    private Intent createShareForecastIntent() {
        Trailer trailer = (Trailer) mTrailerAdapter.getItem(0);
        String trailerPath = trailer.getPath();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailerPath + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }



    @Override
    public void onStart(){
        super.onStart();
        if(null != mUri) {
            getTrailers();
            getReviews();
        }
    }

    public void getTrailers(){
        String mdbId = MovieContract.MovieEntry.getMDBIdFromUri(mUri);

        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getActivity(), mTrailerAdapter);
        fetchTrailerTask.execute(mdbId);
    }

    public void getReviews(){
        String mdbId = MovieContract.MovieEntry.getMDBIdFromUri(mUri);

        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity(), mReviewAdapter);
        fetchReviewsTask.execute(mdbId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if ( null != mUri ) {

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            mFavorite = data.getString(COL_FAVORITE);
            checkIfMovieIsFavorite(mFavorite);

            String posterPath = data.getString(COL_POSTER_PATH);
            Picasso.with(getActivity()).load(posterPath).into(mImageView);

            // Read date from cursor and update views for day of week and date
            String title = data.getString(COL_MOVIE_TITLE);
            mTitleView.setText(title);

            // Read description from cursor and update view
            String releaseDate = data.getString(COL_RELEASE_DATE);
            String releaseYear = Utility.dateToYear(releaseDate);
            mReleaseYearView.setText(releaseYear);

            double rating = data.getInt(COL_USER_RATING);
            setStarRating(rating);

            // For accessibility, add a content description to the icon field
            String overview = data.getString(COL_OVERVIEW);
            mOverviewView.setText(overview);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    void setStarRating(double rating){

        if(rating <= 2.0) mRatingView.setNumStars(1);
        if(rating > 2.0 && rating <= 4.0) mRatingView.setNumStars(2);
        if(rating > 4.0 && rating <= 6.0) mRatingView.setNumStars(3);
        if(rating > 6.0 && rating <= 8.0) mRatingView.setNumStars(4);
        if(rating > 8.0) mRatingView.setNumStars(5);

    }

    void checkIfMovieIsFavorite(String favorite){

        if(favorite.equals(CHECKBOX_CHECKED)){
            mFavoriteCheckbox.setChecked(true);
        }

        if(favorite.equals(CHECKBOX_UNCHECKED)){
            mFavoriteCheckbox.setChecked(false);
        }

    }
}

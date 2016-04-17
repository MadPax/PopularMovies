package com.example.maximilianvoss.popularmoviesmv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.maximilianvoss.popularmoviesmv.sync.PopularMoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null){

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
                mTwoPane = false;
            }

        getSupportActionBar().setElevation(0f);
        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.android_action_bar_spinner_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_order_array, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Object item = parent.getSelectedItem().toString();
                MoviesFragment mf = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.movie_fragment);

                String POPULAR_SEARCH_KEY = "popular";
                String TOP_RATED_SEARCH_KEY = "top_rated";
                String FAVORITE_SEARCH_KEY = "favorite";

                if (item.equals("Popular")) mf.onSortOrderChanged(POPULAR_SEARCH_KEY);
                if (item.equals("Top Rated")) mf.onSortOrderChanged(TOP_RATED_SEARCH_KEY);
                if (item.equals("Favorite")) mf.onSortOrderChanged(FAVORITE_SEARCH_KEY);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        return true;

    }

    @Override
    public void onItemSelected(Uri contentUri) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {

            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}

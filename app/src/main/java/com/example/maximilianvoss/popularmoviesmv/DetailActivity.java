package com.example.maximilianvoss.popularmoviesmv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ruedigervoss on 18/03/16.
 */
public class DetailActivity extends AppCompatActivity {

    private String LOG_TAG = DetailActivity.class.getSimpleName();
    private TrailerAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        getSupportActionBar().setElevation(0f);
    }
}

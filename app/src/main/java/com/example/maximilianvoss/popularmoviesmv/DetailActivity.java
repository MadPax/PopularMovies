package com.example.maximilianvoss.popularmoviesmv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

/**
 * Created by ruedigervoss on 18/03/16.
 */
public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ImageView imageView = (ImageView)rootView.findViewById(R.id.imageView);

            ButterKnife.bind(this, rootView);

            Movie ReceivedMovie = getActivity().getIntent().getParcelableExtra("movie");

            Picasso.with(getActivity()).load(ReceivedMovie.getImage_path()).into(imageView);
            ((TextView)rootView.findViewById(R.id.titleView)).setText(ReceivedMovie.getMovie_name());
            ((TextView)rootView.findViewById(R.id.popularityView)).setText(ReceivedMovie.getUser_rating() + "/10");
            ((TextView)rootView.findViewById(R.id.releaseView)).setText("Release date: " + ReceivedMovie.getRelease_date());
            ((TextView)rootView.findViewById(R.id.overviewView)).setText(ReceivedMovie.getOverview());
            return rootView;
        }
    }
}

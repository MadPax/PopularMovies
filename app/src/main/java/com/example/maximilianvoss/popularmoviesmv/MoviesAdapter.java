package com.example.maximilianvoss.popularmoviesmv;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by ruedigervoss on 17/03/16.
 */
public class MoviesAdapter extends CursorAdapter {

    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    public static class ViewHolder{
        public final ImageView imageView;

        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type

        View view = LayoutInflater.from(context).inflate(R.layout.image_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Picasso.with(context).load(cursor.getString(MoviesFragment.COL_MOVIE_POSTER_PATH)).into(viewHolder.imageView);
        Log.v(LOG_TAG, "In bindView: " + cursor.getString(MoviesFragment.COL_MOVIE_POSTER_PATH));

    }


}

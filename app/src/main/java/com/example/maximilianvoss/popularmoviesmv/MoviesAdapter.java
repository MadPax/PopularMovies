package com.example.maximilianvoss.popularmoviesmv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ruedigervoss on 17/03/16.
 */
public class MoviesAdapter extends BaseAdapter {
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Movie> currentMovieList;

    public MoviesAdapter(Context context, ArrayList<Movie> list){
        this.mContext = context;
        this.currentMovieList = list;
        notifyDataSetChanged();
    }

    public void addAll(ArrayList movieList) {
        if(currentMovieList==null){
            currentMovieList = new ArrayList();
        }
        currentMovieList.clear();
        currentMovieList.addAll(movieList);
        notifyDataSetChanged();
    }

    public static class ViewHolder{
        @Bind(R.id.imageView) ImageView imageView;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getCount(){
        return currentMovieList.size();
    }

    @Override
    public Movie getItem(int position) {
        return currentMovieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder imageHolder;

        if(convertView == null){
            LayoutInflater inflater = ((MainActivity)mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.image_view, parent, false);
            imageHolder = new ViewHolder(convertView);
            imageHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
            convertView.setTag(imageHolder);
        } else {
            imageHolder = (ViewHolder)convertView.getTag();
        }

        Picasso.with(mContext).load(currentMovieList.get(position).getImage_path()).into(imageHolder.imageView);

        return convertView;
    }


}

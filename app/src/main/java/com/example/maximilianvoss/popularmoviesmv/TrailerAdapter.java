package com.example.maximilianvoss.popularmoviesmv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ruedigervoss on 07/04/16.
 */
public class TrailerAdapter extends BaseAdapter {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private ArrayList<Trailer> mArrayList;
    private Context mContext;

    public TrailerAdapter(Context context, ArrayList arrayList){
        mArrayList = arrayList;
        mContext = context;
        notifyDataSetChanged();
    }

    public void addAll(ArrayList movieList) {
        if(mArrayList==null){
            mArrayList = new ArrayList();
        }
        mArrayList.clear();
        mArrayList.addAll(movieList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mArrayList.size();
    }

    @Override
    public Trailer getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{

        public final TextView nameView;

        public ViewHolder(View view){
            nameView = (TextView)view.findViewById(R.id.trailer_listview_name);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View view = convertView;
        ViewHolder viewHolder;

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.trailer_listview_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        final Trailer trailer = getItem(position);

        viewHolder.nameView.setText(trailer.getName());

        Log.v("TrailerAdapter getView", "This was run for item position n˚" + position);

        return view;
    }

}

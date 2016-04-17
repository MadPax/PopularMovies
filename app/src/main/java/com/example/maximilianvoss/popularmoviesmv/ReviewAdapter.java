package com.example.maximilianvoss.popularmoviesmv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ruedigervoss on 08/04/16.
 */
public class ReviewAdapter extends BaseAdapter {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private ArrayList<Review> mArrayList;
    private Context mContext;

    public ReviewAdapter(Context context, ArrayList arrayList){
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
    public Review getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{

        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view){
            authorView = (TextView)view.findViewById(R.id.review_author);
            contentView = (TextView)view.findViewById(R.id.review_content);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View view = convertView;
        ViewHolder viewHolder;

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.review_listview_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        final Review review = getItem(position);

        viewHolder.authorView.setText(review.getAuthor());
        viewHolder.contentView.setText(review.getContent());

        return view;
    }

}

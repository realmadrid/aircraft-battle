package edu.anu.comp6442.retrogame2018s1.adapter;

/*
 * Copyright (C) 2018,
 *
 * Yang Zheng <u6287751@anu.edu.au>
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.anu.comp6442.retrogame2018s1.R;
import edu.anu.comp6442.retrogame2018s1.model.Scoreboard;

/**
 *
 * A adapter for the rank list view
 */

public class RankAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Scoreboard mData; // data for showing
    private int mResourceId; // layout for item view
    // image resource for different ranking
    private int[] imageList = new int[]{R.drawable.enemy4, R.drawable.enemy3,
            R.drawable.enemy2, R.drawable.enemy1};

    public RankAdapter(LayoutInflater inflater, Scoreboard data, int resourceId) {
        mInflater = inflater;
        mData = data;
        mResourceId = resourceId;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Scoreboard.Record record = mData.get(position);
        View itemView;
        ViewHolder viewHolder;
        // if null, create new views
        if (convertView == null) {
            itemView = mInflater.inflate(mResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) itemView.findViewById(R.id.item_img);
            viewHolder.img1 = (ImageView) itemView.findViewById(R.id.item_img1);
            viewHolder.scoreView = (TextView) itemView.findViewById(R.id.item_score);
            viewHolder.dateView = (TextView) itemView.findViewById(R.id.item_date);

            itemView.setTag(viewHolder);
        } else { // reuse the created views
            itemView = convertView;
            viewHolder = (ViewHolder) itemView.getTag();
        }
        // set the values for different views
        setImageView(position, viewHolder.img, viewHolder.img1);
        viewHolder.scoreView.setText("Score: " + record.score);
        viewHolder.dateView.setText("Date: " + record.date);

        return itemView;
    }

    /**
     * uet different iamges for different ranking positions
     * @param position ranking position
     * @param img   image view for showing lager image
     * @param img1  image view for showing smaller image
     */
    private void setImageView(int position, ImageView img, ImageView img1) {
        if (position < 3){
            img.setImageResource(imageList[position]);
            img1.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
            img.setRotation(180);
        }else{
            img1.setImageResource(imageList[3]);
            img.setVisibility(View.GONE);
            img1.setVisibility(View.VISIBLE);
            img1.setRotation(180);
        }

    }

    class ViewHolder {
        ImageView img;
        ImageView img1;
        TextView scoreView;
        TextView dateView;
    }
}

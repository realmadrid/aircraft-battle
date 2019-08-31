package edu.anu.comp6442.retrogame2018s1.activity;

/*
 * Copyright (C) 2018,
 *
 * Yang Zheng <u6287751@anu.edu.au>
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import edu.anu.comp6442.retrogame2018s1.R;
import edu.anu.comp6442.retrogame2018s1.adapter.RankAdapter;
import edu.anu.comp6442.retrogame2018s1.model.Scoreboard;

/**
 * Created by admin on 2018/5/16.
 *
 * A activity for showing the ranking score
 */

public class RankActivity extends AppCompatActivity {

    private ListView rankListView;
    private Scoreboard scoreboard;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rank);
        initViews();
    }

    public void initViews() {
        // get list view
        rankListView = (ListView) findViewById(R.id.rank_list);
        // get score data
        scoreboard = new Scoreboard(RankActivity.this);
        // create adapter for the list view
        RankAdapter rankAdapter = new RankAdapter(getLayoutInflater(), scoreboard, R.layout.list_item);
        //  Log.i("info score num", ""+scoreboard.size());
        // set adapter to the list view
        rankListView.setAdapter(rankAdapter);
    }

}

package com.example.android.plants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton searchButton;
    private ImageButton settingsButton;
    private ImageView no_plants_view;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private static ArrayList<String> my_plants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // see if we already have a plants array, and use it if we do
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Set<String> set = prefs.getStringSet("my_plants_set", null);
        if (set == null) {
            my_plants = new ArrayList<>();
        } else {
            my_plants = new ArrayList<String>(set);
        }



        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerAdapter(my_plants);
        mRecyclerView.setAdapter(mAdapter);

        // takes us to search page
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });

        // take us to settings page
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        // get plant name if we're coming from plant page (added plant)
        String plant_name = prefs.getString("plant_name", "no_id");
        if (!plant_name.equals("no_id")) {
            my_plants.add(plant_name);
            editor = prefs.edit();
            editor.remove("plant_name");
            editor.apply();
        }

        if (!my_plants.isEmpty()) {
            no_plants_view = (ImageView) findViewById(R.id.no_plant_msg);
            no_plants_view.setVisibility(View.GONE);
        }
    }

    // all this part is to save the plant array on the sharedPrefrences
    // after we leave this activity
    @Override
    public void onPause(){
        super.onPause();
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor = prefs.edit();

        Set<String> set = new HashSet<String>();
        set.addAll(my_plants);
        editor.putStringSet("my_plants_set", set);
        editor.commit();
    }
}

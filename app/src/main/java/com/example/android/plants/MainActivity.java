package com.example.android.plants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton searchButton;
    private ImageButton settingsButton;
    private ImageView no_plants_view;
    private DatabaseReference mDatabase;

    private static ArrayList<String> my_plants = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                finish();
            }
        });

        // take us to settings page
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                finish();
            }
        });

        if (!my_plants.isEmpty()) {
            no_plants_view = (ImageView) findViewById(R.id.no_plant_msg);
            no_plants_view.setVisibility(View.GONE);
        }
    }

    @Override
    // called when we get to this activity using back button
    public void onResume(){
        super.onResume();

        //get database
        mDatabase = FirebaseDatabase.getInstance().getReference("Plants");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                my_plants.clear();
                boolean hasPlants = false;
                for (DataSnapshot parent : dataSnapshot.getChildren()) {
                    String plant_name = parent.getKey();
                    if (parent.child("Planted").getValue(String.class).equals("True") && !my_plants.contains(plant_name)) {
                        hasPlants = true;
                        my_plants.add(plant_name);
                    }
                }
                mAdapter.notifyDataSetChanged();

                no_plants_view = (ImageView) findViewById(R.id.no_plant_msg);
                if (!hasPlants) {
                    no_plants_view.setVisibility(View.VISIBLE);
                } else {
                    no_plants_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

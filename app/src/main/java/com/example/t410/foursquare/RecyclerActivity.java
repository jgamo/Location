package com.example.t410.foursquare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends AppCompatActivity {

    private List<RView> details;
    private RecyclerView rv;
    private String lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_activity);
        Log.d("animal", "entree");
        /*Bundle datos = this.getIntent().getExtras();
        lat = getIntent().getStringExtra("latitude");
        lon = getIntent().getStringExtra("longitude");*/

        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        Log.d("animal",String.valueOf(MainActivity.lista.size()));
        for (int i = 0; i < MainActivity.lista.size() ; i++) {
            Log.d("animal", "hi bitch"+MainActivity.lista.get(i).getId()+ "_"
                    +MainActivity.lista.get(i).getName());
        }
        //initializeData();
        initializeAdapter();
    }

    private void initializeData(){
        details = new ArrayList<>();
        details.add(new RView("Latitude", lat, R.drawable.loc));
        details.add(new RView("Longitude", lon, R.drawable.loc));
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(com.example.t410.foursquare.MainActivity.lista,this);
        rv.setAdapter(adapter);
    }
}

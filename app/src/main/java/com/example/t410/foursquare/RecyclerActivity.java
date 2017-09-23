package com.example.t410.foursquare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends AppCompatActivity {
    private RecyclerViewClickListener listener;
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
            Log.d("animal", MainActivity.lista.get(i).getId()+ "_"+MainActivity.lista.get(i).getName()+ "_"+MainActivity.lista.get(i).getDistance()+"_"+MainActivity.lista.get(i).getUrlPict()+"_"+MainActivity.lista.get(i).getAddress());
        }
        initializeAdapter();
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(com.example.t410.foursquare.MainActivity.lista,this, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(RecyclerActivity.this, "Elemento " + position, Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);
    }
}

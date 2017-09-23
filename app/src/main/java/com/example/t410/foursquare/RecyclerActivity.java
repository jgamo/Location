package com.example.t410.foursquare;

import android.content.Intent;
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
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_activity);

        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        /*Log.d("animal",String.valueOf(MainActivity.lista.size()));
        for (int i = 0; i < MainActivity.lista.size() ; i++) {
            Log.d("animal", MainActivity.lista.get(i).getId()+ "_"+MainActivity.lista.get(i).getName()+ "_"+MainActivity.lista.get(i).getDistance()+"_"+MainActivity.lista.get(i).getUrlPict()+"_"+MainActivity.lista.get(i).getAddress());
        }*/
        initializeAdapter();
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(com.example.t410.foursquare.MainActivity.lista,this, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent act = new Intent(RecyclerActivity.this, ScrollingActivity.class);
                act.putExtra("indice", String.valueOf(position));
                startActivity(act);
                Toast.makeText(RecyclerActivity.this, "Elemento " + position, Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);
    }
}

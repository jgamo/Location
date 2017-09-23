package com.example.t410.foursquare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int indice = Integer.parseInt(getIntent().getStringExtra("indice"));
        TextView tvName = (TextView)findViewById(R.id.venue_name);
        TextView tvDes = (TextView)findViewById(R.id.venue_extra);
        ImageView img = (ImageView)findViewById(R.id.venue_photo);

        if (MainActivity.lista.get(indice).getUrlPict()!=null) {
            Glide.with(img.getContext())
                    .load(MainActivity.lista.get(indice).getUrlPict())
                    .crossFade()
                    .centerCrop()
                    .into(img);
        } else {
            img.setImageResource(R.drawable.loc);;
        }

        tvName.setText(MainActivity.lista.get(indice).getName());
        tvDes.setText("Distance: "+String.valueOf(MainActivity.lista.get(indice).getDistance())+" meters");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not implemented yet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}

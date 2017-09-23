package com.example.t410.foursquare;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by T410 on 19/09/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
    private Context context;
    private RecyclerViewClickListener listener;

    ArrayList<FQ> persons;

    public RVAdapter(ArrayList<FQ> persons, Context context, RecyclerViewClickListener listener){
        this.persons = persons;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return new RowViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.personName.setText(persons.get(position).getName());
        holder.personAge.setText(persons.get(position).getAddress());
        //ImageView imagen = (ImageView) v.findViewById(R.id.imagenItem);
        if (persons.get(position).getUrlPict()!=null) {
            Glide.with(context)
                    .load(persons.get(position).getUrlPict())
                    .crossFade()
                    .centerCrop()
                    .into(holder.personPhoto);
            // holder.personPhoto.setImageResource(persons.get(i).photoId);
        } else {
            holder.personPhoto.setImageResource(R.drawable.loc);
        }
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personAge = (TextView)itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

package com.example.recipeapp;


import android.content.Context;
import android.content.Intent;
import android.icu.number.Scale;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class recipeAdapter extends RecyclerView.Adapter<RecipeViewHolder>{

    private Context c;
    private List<RecipeModel> seznamReceptov;

    public recipeAdapter(Context c, List<RecipeModel> seznamReceptov) {
        this.c = c;
        this.seznamReceptov = seznamReceptov;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recept,parent,false);

        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {

        Glide.with(c)
                .load(seznamReceptov.get(position).getRecipePhoto())
                        .into(RecipeViewHolder.slika);

        holder.ime.setText(seznamReceptov.get(position).getRecipeName());
        holder.sestavine.setText(seznamReceptov.get(position).getIngredients());
        holder.vrsta.setText(seznamReceptov.get(position).getMealType());
        holder.navodila.setText(seznamReceptov.get(position).getInstructions());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, RecipeInfoActivity.class);
                intent.putExtra("ID", seznamReceptov.get(holder.getAdapterPosition()).getRecipeID());
                intent.putExtra("Image", seznamReceptov.get(holder.getAdapterPosition()).getRecipePhoto());
                intent.putExtra("Title", seznamReceptov.get(holder.getAdapterPosition()).getRecipeName());
                intent.putExtra("MealType", seznamReceptov.get(holder.getAdapterPosition()).getMealType());
                intent.putExtra("Ingredients", seznamReceptov.get(holder.getAdapterPosition()).getIngredients());
                intent.putExtra("Instructions", seznamReceptov.get(holder.getAdapterPosition()).getInstructions());
                c.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return seznamReceptov.size();
    }

    public void filteredList(ArrayList<RecipeModel> filtriranSeznam) {
        seznamReceptov = filtriranSeznam;
        notifyDataSetChanged();
    }
}

class RecipeViewHolder extends RecyclerView.ViewHolder {

    static ImageView slika;
    TextView ime, vrsta, sestavine, navodila;
    CardView cv;

    public RecipeViewHolder(View itemView) {
        super(itemView);

        slika = itemView.findViewById(R.id.receptIV);
        ime = itemView.findViewById(R.id.imeReceptaTV);
        vrsta = itemView.findViewById(R.id.vrstaObrokaTV);
        sestavine = itemView.findViewById(R.id.sestavineTV);
        navodila = itemView.findViewById(R.id.navodilaTV);

        cv = itemView.findViewById(R.id.seznamReceptovCV);


    }
}

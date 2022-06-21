package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RecipeInfoActivity extends AppCompatActivity {

    TextView ime, sestavine, navodila;
    ImageView slika;
    String imageUrl = "";
    String recipeID, vrstaObroka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        ime = (TextView) findViewById(R.id.titleTV);
        sestavine = (TextView) findViewById(R.id.ingredientsTV);
        navodila = (TextView) findViewById(R.id.descriptionTV);
        slika = (ImageView) findViewById(R.id.imageIV);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            recipeID = bundle.getString("ID");
            imageUrl = bundle.getString("Image");
            ime.setText(bundle.getString("Title"));
            vrstaObroka = bundle.getString("MealType");
            sestavine.setText(bundle.getString("Ingredients"));
            navodila.setText(bundle.getString("Instructions"));

            Glide.with(this)
                    .load(bundle.getString("Image"))
                    .into(slika);

        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        finish();
    }

    public void deleteRecipeBTN(View view) {

        final DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("Recipe");
        FirebaseStorage fbs = FirebaseStorage.getInstance();
        StorageReference sr = fbs.getReferenceFromUrl(imageUrl);

        sr.delete().addOnSuccessListener(unused -> {
            dbr.child(recipeID).removeValue();
            Toast.makeText(RecipeInfoActivity.this, "Recipe deleted.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        });
    }

    public void editRecipeBTN(View view) {

        startActivity(new Intent(getApplicationContext(),EditRecipeActivity.class)
                .putExtra("RecipeID", recipeID)
                .putExtra("ImageURL", imageUrl)
                .putExtra("RecipeName", ime.getText().toString())
                .putExtra("MealType", vrstaObroka)
                .putExtra("Ingredients", sestavine.getText().toString())
                .putExtra("Instructions", navodila.getText().toString())
        );
    }
}
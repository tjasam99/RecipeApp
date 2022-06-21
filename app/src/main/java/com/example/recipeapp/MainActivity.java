package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference dbr;
    private ValueEventListener vel;
    ProgressDialog pd;

    recipeAdapter adapter;

    EditText searchText;

    RecyclerView rv;
    List<RecipeModel> seznamReceptov;

    String filterMeals;

    int changes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner mealTypes = (Spinner) findViewById(R.id.mealTypeS);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filterMealTypes_array, R.layout.spinnerstyle);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypes.setAdapter(spinnerAdapter);

        mealTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        if (changes ==0) return;
                        filterMeals = getResources().getString(R.string.all);
                        break;
                    case 1:
                        filterMeals = getResources().getString(R.string.breakfast);
                        changes++;
                        break;
                    case 2:
                        filterMeals = getResources().getString(R.string.lunch);
                        changes++;
                        break;
                    case 3:
                        filterMeals = getResources().getString(R.string.dinner);
                        changes++;
                        break;
                    case 4:
                        filterMeals = getResources().getString(R.string.dessert);
                        changes++;
                        break;
                    case 5:
                        filterMeals = getResources().getString(R.string.snack);
                        changes++;
                        break;
                }
                filterMeals(filterMeals);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        rv = (RecyclerView)findViewById(R.id.seznamReceptovRV);

        GridLayoutManager glm = new GridLayoutManager(MainActivity.this, 1);
        rv.setLayoutManager(glm);

        searchText = (EditText)findViewById(R.id.searchET);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading recipes...");

        seznamReceptov = new ArrayList<>();

        adapter = new recipeAdapter(MainActivity.this, seznamReceptov);
        rv.setAdapter(adapter);

        dbr = FirebaseDatabase.getInstance().getReference("Recipe");

        pd.show();
        vel = dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seznamReceptov.clear();

                for(DataSnapshot item: snapshot.getChildren()){

                    RecipeModel recept = item.getValue(RecipeModel.class);
                    seznamReceptov.add(recept);

                }

                adapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
            }
        });


            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    filter(editable.toString());
                }
            });


    }

    private void filterMeals(String text) {

        ArrayList<RecipeModel> filtriranSeznam = new ArrayList<>();


        for (RecipeModel data: seznamReceptov) {
            if (data.getMealType().toLowerCase().contains(text.toLowerCase()) || text.equals("all")){
                filtriranSeznam.add(data);
            }
        }

        adapter.filteredList(filtriranSeznam);

    }

    private void filter(String text) {

        ArrayList<RecipeModel> filtriranSeznam = new ArrayList<>();

        for (RecipeModel data: seznamReceptov) {
            if (data.getIngredients().toLowerCase().contains(text.toLowerCase())){
                filtriranSeznam.add(data);
            }
        }

        adapter.filteredList(filtriranSeznam);


    }

    public void addRecipeBTN(View view) {

        startActivity(new Intent(this, NewRecipeActivity.class));
    }
}
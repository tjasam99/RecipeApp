package com.example.recipeapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditRecipeActivity extends AppCompatActivity {

    ImageView recipeImage;
    String mealType, imageUrl, newImageUrl, newMealType;
    String newName, newIngredients, newInstructions;
    EditText recipeName, recipeIngredients, recipeInstructions;
    Uri saveuri;
    Spinner mealTypes;

    String recipeID;

    DatabaseReference dbr;
    StorageReference sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        recipeImage = (ImageView) findViewById(R.id.recipeImageIV);
        recipeName = (EditText) findViewById(R.id.recipeNameET);
        recipeIngredients = (EditText) findViewById(R.id.recipeIngredientsET);
        recipeInstructions = (EditText) findViewById(R.id.recipeInstructionsET);

        mealTypes = (Spinner) findViewById(R.id.mealTypeS);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.mealTypes_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypes.setAdapter(spinnerAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            recipeID = bundle.getString("RecipeID");
            imageUrl = bundle.getString("ImageURL");
            Glide.with(EditRecipeActivity.this).
                    load(imageUrl).
                    into(recipeImage);
            recipeName.setText(bundle.getString("RecipeName"));
            mealType = bundle.getString("MealType");
            recipeIngredients.setText(bundle.getString("Ingredients"));
            recipeInstructions.setText(bundle.getString("Instructions"));

        }

        newImageUrl = imageUrl;

        switch (mealType) {
            case "breakfast":
                mealTypes.setSelection(0);
                break;
            case "lunch":
                mealTypes.setSelection(1);
                break;
            case "dinner":
                mealTypes.setSelection(3);
                break;
            case "dessert":
                mealTypes.setSelection(4);
                break;
            case "snack":
                mealTypes.setSelection(5);
                break;
        }

        mealTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        newMealType = getResources().getString(R.string.breakfast);
                        break;
                    case 1:
                        newMealType = getResources().getString(R.string.lunch);
                        break;
                    case 2:
                        newMealType = getResources().getString(R.string.dinner);
                        break;
                    case 3:
                        newMealType = getResources().getString(R.string.dessert);
                        break;
                    case 4:
                        newMealType = getResources().getString(R.string.snack);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dbr = FirebaseDatabase.getInstance().getReference("Recipe").child(recipeID);

    }

    ActivityResultLauncher<String> selectImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null && !uri.equals(Uri.EMPTY)) {
                        recipeImage.setImageURI(uri);
                        saveuri = uri;

                    } else {
                        recipeImage.setImageResource(R.drawable.uploadimage);
                        Toast.makeText(EditRecipeActivity.this, "Please select an image!", Toast.LENGTH_LONG).show();
                    }
                }
            });


    public void selectImageBTN(View view) {
        selectImage.launch("image/*");
    }

    public void updateRecipeBTN(View view) {

        newName = recipeName.getText().toString().trim();
        newIngredients = recipeIngredients.getText().toString().trim();
        newInstructions = recipeInstructions.getText().toString().trim();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Recipe updating....");
        pd.show();

        if (saveuri == null) {
            updateRecipe();
            pd.dismiss();
        }

        else {
            sr = FirebaseStorage.getInstance()
                    .getReference().child("RecipeImage").child(saveuri.getLastPathSegment());

            sr.putFile(saveuri).addOnSuccessListener(taskSnapshot -> {

                Task<Uri> ut = taskSnapshot.getStorage().getDownloadUrl();
                while (!ut.isComplete()) ;
                Uri url = ut.getResult();
                newImageUrl = url.toString();
                updateRecipe();

            }).addOnFailureListener(e -> {
                pd.dismiss();
            });
        }

    }

    public void updateRecipe() {

        RecipeModel recept = new RecipeModel(
                recipeID,
                newName,
                newMealType,
                newIngredients,
                newInstructions,
                newImageUrl
        );

        dbr.setValue(recept).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    if (!(imageUrl.equals(newImageUrl))){
                        StorageReference newsr = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                        newsr.delete();
                    }

                    Toast.makeText(EditRecipeActivity.this, "Recipe updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }
}
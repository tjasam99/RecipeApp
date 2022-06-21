package com.example.recipeapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Calendar;

public class NewRecipeActivity extends AppCompatActivity {

    ImageView recipeImage;
    String mealType, imageUrl;
    EditText recipeName, recipeIngredients, recipeInstructions;
    Uri saveuri;

    String recipeID;

    long index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("Recipe");
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    index = (snapshot.getChildrenCount());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recipeImage = (ImageView)findViewById(R.id.recipeImageIV);
        recipeName = (EditText)findViewById(R.id.recipeNameET);
        recipeIngredients = (EditText)findViewById(R.id.recipeIngredientsET);
        recipeInstructions = (EditText)findViewById(R.id.recipeInstructionsET);
        mealType = getResources().getString(R.string.breakfast);

        Spinner mealTypes = (Spinner) findViewById(R.id.mealTypeS);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.mealTypes_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypes.setAdapter(spinnerAdapter);

        mealTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        mealType = getResources().getString(R.string.breakfast);
                        break;
                    case 1:
                        mealType = getResources().getString(R.string.lunch);
                        break;
                    case 2:
                        mealType = getResources().getString(R.string.dinner);
                        break;
                    case 3:
                        mealType = getResources().getString(R.string.dessert);
                        break;
                    case 4:
                        mealType = getResources().getString(R.string.snack);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                        Toast.makeText(NewRecipeActivity.this, "Please select an image!", Toast.LENGTH_LONG).show();
                    }
                }
            });

    public void selectImageBTN(View view) {
        selectImage.launch("image/*");
    }

    public void uploadImage() {

        StorageReference sr = FirebaseStorage.getInstance().getReference().child("RecipeImage").child(saveuri.getLastPathSegment());

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Saving recipe....");
        pd.show();

        sr.putFile(saveuri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> ut = taskSnapshot.getStorage().getDownloadUrl();
            while(!ut.isComplete());
            Uri url = ut.getResult();
            imageUrl = url.toString();
            uploadRecipe();
            pd.dismiss();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewRecipeActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG);
                pd.dismiss();
            }
        });

    }

    public void saveRecipeBTN(View view) {

        uploadImage();

    }

    public void uploadRecipe() {

        recipeID = String.valueOf((index+1));

        RecipeModel recept = new RecipeModel(
                recipeID,
                recipeName.getText().toString(),
                mealType,
                recipeIngredients.getText().toString(),
                recipeInstructions.getText().toString(),
                imageUrl
        );

        FirebaseDatabase.getInstance().getReference("Recipe")
                .child(recipeID).setValue(recept).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(NewRecipeActivity.this, "Recipe added!", Toast.LENGTH_SHORT).show();

                            finish();

                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        }
                });


    }
}
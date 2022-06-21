package com.example.recipeapp;

public class RecipeModel {

    private String recipeID;
    private String recipeName;
    private String mealType;
    private String ingredients;
    private String instructions;
    private String recipePhoto;

    public RecipeModel() {
    }

    public RecipeModel(String recipeID, String recipeName, String mealType, String ingredients, String instructions, String recipePhoto) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.mealType = mealType;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.recipePhoto = recipePhoto;
    }

    @Override
    public String toString() {
        return "RecipeModel{" +
                "recipeID=" + recipeID +
                ", recipeName='" + recipeName + '\'' +
                ", mealType='" + mealType + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", instructions='" + instructions + '\'' +
                ", recipePhoto='" + recipePhoto + '\'' +
                '}';
    }

    public String getRecipeID() {return recipeID;}

    public String getRecipeName() {
        return recipeName;
    }

    public String getMealType() {
        return mealType;
    }

    public String getIngredients() {return ingredients;}

    public String getInstructions() {
        return instructions;
    }

    public String getRecipePhoto() {
        return recipePhoto;
    }

}

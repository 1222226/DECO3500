package com.example.test2.Database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Ingredients {

    private DatabaseReference databaseReference;

    public Ingredients() {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = database.getReference("ingredients");
    }

        public void addIngredients() {
        //ingredients name and corressponding colorcode for test
        String[][] ingredients = {
                {"Chicken", "#F8E5C3"},
                {"Salmon", "#FF8C69"},
                {"Beef", "#A52A2A"},
                {"Pork", "#FFC0CB"},
                {"Avocado", "#568203"},
                {"Bacon", "#D2691E"},
                {"Bread", "#F5DEB3"},
                {"Cheese", "#FFD700"},
                {"Rice", "#FFFFF0"},
                {"Egg", "#FFFACD"},
                {"Tomatoes", "#FF6347"},
                {"Potatoes", "#D2B48C"},
                {"Oats", "#EEDC82"},
                {"Tuna", "#8A9A5B"},
                {"Egg Plants", "#9370DB"}
        };


        for (String[] ingredient : ingredients) {
            String name = ingredient[0];
            String color = ingredient[1];
            saveIngredientToDatabase(name, color, "");
        }
    }


    private void saveIngredientToDatabase(String name, String color, String imageUrl) {
        Map<String, Object> ingredient = new HashMap<>();
        ingredient.put("name", name);
        ingredient.put("color", color);
        ingredient.put("image_url", imageUrl);


        databaseReference.child(name).setValue(ingredient)
                .addOnSuccessListener(aVoid -> {

                    System.out.println("Ingredient " + name + " added successfully.");
                })
                .addOnFailureListener(e -> {

                    System.err.println("Error adding ingredient " + name);
                });
    }
}

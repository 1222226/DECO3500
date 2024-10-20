package com.example.test2.Database;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*public class Groupping {
    public String time;
    public String recipe;
    public GroupingUser user1;
        public GroupingUser user2;
    public Groupping(){

    }
    public Groupping(String time, String recipe, GroupingUser user1, GroupingUser user2) {
        this.time = time;
        this.recipe = recipe;
        this.user1 = user1;
        this.user2 = user2;
    }
}*/



import java.util.HashMap;
import java.util.Map;



import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class Grouping {

    private DatabaseReference databaseReference;
    private static final String TAG = "KitchenUpload";

    public Grouping() {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = database.getReference("Kitchens");
    }

    // setup 6 kitchens
    public void addKitchens() {
        Log.d(TAG, "addKitchens method executed");
        for (int i = 1; i <= 6; i++) {
            saveKitchenToDatabase(i);
        }
    }


    private void saveKitchenToDatabase(int kitchenNo) {
        Map<String, Object> kitchen = new HashMap<>();
        kitchen.put("User1", "");
        kitchen.put("User2", "");
        kitchen.put("user1Ingredients", "");
        kitchen.put("user2Ingredients", "");
        kitchen.put("user1Meal", "");
        kitchen.put("user2Meal", "");


        databaseReference.child(String.valueOf(kitchenNo)).setValue(kitchen)
                .addOnSuccessListener(aVoid -> {


                    Log.d(TAG, "Kitchen " + kitchenNo + " added successfully.");
                })
                .addOnFailureListener(e -> {

                    Log.d(TAG, "Error adding Kitchen " + kitchenNo);
                });
    }
}




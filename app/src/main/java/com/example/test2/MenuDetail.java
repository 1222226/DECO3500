package com.example.test2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//this class fetch the detail of a meal
public class MenuDetail extends AppCompatActivity {

    private ImageView recipeImage;
    private TextView recipeDescription;
    private Button backButton, agreeButton;
    private RequestQueue requestQueue;
    private static String TAG="MenuDetail";

    private static int kitchenNo;
    private String currentUserID;
    private String mealName;

    private DatabaseReference databaseReference;

    private String databaseUrl = "https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);


        recipeImage = findViewById(R.id.recipe_image);
        recipeDescription = findViewById(R.id.recipe_description);
        backButton = findViewById(R.id.back_button);
        agreeButton = findViewById(R.id.agree_button);

        // find the recipe name from previous activity
        String mealNameKitchenNo = getIntent().getStringExtra("mealName");
        Log.d(TAG,mealNameKitchenNo);
        if (mealNameKitchenNo != null && mealNameKitchenNo.contains(",")) {
            String[] ingredientArray = mealNameKitchenNo.split(",");
           mealName = ingredientArray[0].trim();
         ;
            kitchenNo = Integer.parseInt(ingredientArray[1].trim());

        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            currentUserID = currentUser.getUid();
            //Log.d(TAG, "Current UserID: " + currentUserID);  // 添加日志来检查 UserID
        }
        databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Kitchens/" + kitchenNo);


        requestQueue = Volley.newRequestQueue(this);


        fetchMealDetails(mealName);


        backButton.setOnClickListener(v -> finish());


        agreeButton.setOnClickListener(v -> {
            if (currentUserID != null) {
                updateUserMealSelection(mealName);
                finish();
            } else {
                Toast.makeText(MenuDetail.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //update the current user's selection to Database
    private void updateUserMealSelection(String selectedMeal) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user1ID = dataSnapshot.child("User1").getValue(String.class);
                String user2ID = dataSnapshot.child("User2").getValue(String.class);

                // check if current user user1 or user2
                if (user1ID != null && user1ID.equals(currentUserID)) {

                    databaseReference.child("user1Meal").setValue(selectedMeal).addOnSuccessListener(aVoid -> {
                        Toast.makeText(MenuDetail.this, "User1 meal updated to " + selectedMeal, Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(MenuDetail.this, "Failed to update User1 meal", Toast.LENGTH_SHORT).show();
                    });
                } else if (user2ID != null && user2ID.equals(currentUserID)) {

                    databaseReference.child("user2Meal").setValue(selectedMeal).addOnSuccessListener(aVoid -> {
                        Toast.makeText(MenuDetail.this, "User2 meal updated to " + selectedMeal, Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(MenuDetail.this, "Failed to update User2 meal", Toast.LENGTH_SHORT).show();
                    });
                } else {

                    Toast.makeText(MenuDetail.this, "Current user is not assigned to this kitchen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MenuDetail.this, "Failed to read database", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // fetch detail from MealDB
    private void fetchMealDetails(String mealName) {
        String url = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + mealName;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mealsArray = response.getJSONArray("meals");
                            if (mealsArray.length() > 0) {
                                JSONObject meal = mealsArray.getJSONObject(0);


                                String imageUrl = meal.getString("strMealThumb");
                                String instructions = meal.getString("strInstructions");


                                displayMealDetails(imageUrl, instructions);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }


    private void displayMealDetails(String imageUrl, String instructions) {
        // use Gilde to load image
        Glide.with(this).load(imageUrl).into(recipeImage);


        recipeDescription.setText(instructions);
    }


}

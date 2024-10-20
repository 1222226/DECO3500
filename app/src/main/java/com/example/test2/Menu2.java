package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/*public class Menu2 extends AppCompatActivity {
    private String ingredient1;
    private String ingredient2;
    private Button pinaaple_detail;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu2);
        Intent intent = getIntent();
        String ingredients = intent.getStringExtra("from");
        if (ingredients != null && ingredients.contains(",")) {
            String[] ingredientArray = ingredients.split(",");  // 分割成数组
            if (ingredientArray.length >= 2) {
                ingredient1 = ingredientArray[0].trim();  // 去掉前后空格并赋值
                ingredient2 = ingredientArray[1].trim();
            }
        }



       pinaaple_detail = findViewById(R.id.recipe_button1);
        pinaaple_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        Button select_button = findViewById(R.id.select_button);
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count==1){
                    Intent intent = new Intent(Menu2.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu2.this);
        builder.setTitle("Detail")
                .setMessage("xxxxxxxxxxxx \nxxxxxxxxxxx \nxxxxxxxxxxx")
                .setPositiveButton("Agree", (dialog, which) -> {
                    pinaaple_detail.setBackgroundColor(ContextCompat.getColor(Menu2.this, R.color.place_btn2));
                    count=1;
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    pinaaple_detail.setBackgroundColor(ContextCompat.getColor(Menu2.this, R.color.place_btn3));
                    count=0;
                    dialog.dismiss();
                });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}*/
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
//this class list the recommend recipe menu by searching from The MealDB
//Using Volley to get API
public class Menu2 extends AppCompatActivity {

    private LinearLayout layout;
    private String ingredient1;
    private String ingredient2;
    private RequestQueue requestQueue;

    private int kitchenNo;

    private static final String TAG = "Menu2";

    private String databaseUrl = "https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private DatabaseReference databaseReference;
    private String user1Meal = "";
    private String user2Meal = "";

    private String user1 = "";
    private String user2 = "";
    private boolean activityRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);


        String ingredients = getIntent().getStringExtra("ingredients");
        if (ingredients != null && ingredients.contains(",")) {
            String[] ingredientArray = ingredients.split(",");
            ingredient1 = ingredientArray[0].trim();
            ingredient2 = ingredientArray[1].trim();
            kitchenNo = Integer.parseInt(ingredientArray[2].trim());

        }
        databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Kitchens/" + kitchenNo);

        layout = findViewById(R.id.recipe_container);
        requestQueue = Volley.newRequestQueue(this);

        fetchMealsByIngredients(ingredient1, ingredient2);
        fetchUserMealsAndUpdateButtons();
    }

    private void fetchMealsByIngredients(String ingredient1, String ingredient2) {
        // use 2 quests to get recipe menu contains ingredient1 and ingredient2 saperatly
        String url1 = "https://www.themealdb.com/api/json/v1/1/filter.php?i=" + ingredient1;
        String url2 = "https://www.themealdb.com/api/json/v1/1/filter.php?i=" + ingredient2;


        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<String> mealsWithIngredient1 = new ArrayList<>();
                            JSONArray mealsArray1 = response.getJSONArray("meals");
                            StringBuilder ingredient1Meals = new StringBuilder();
                            for (int i = 0; i < mealsArray1.length(); i++) {
                                JSONObject meal = mealsArray1.getJSONObject(i);
                                String mealName = meal.getString("strMeal");
                                mealsWithIngredient1.add(mealName);
                                ingredient1Meals.append(mealName).append(", ");
                            }

                            Log.d(TAG, ingredient1Meals.toString());


                            JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url2, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                ArrayList<String> mealsWithIngredient2 = new ArrayList<>();
                                                JSONArray mealsArray2 = response.getJSONArray("meals");
                                                StringBuilder ingredient2Meals = new StringBuilder();
                                                for (int i = 0; i < mealsArray2.length(); i++) {
                                                    JSONObject meal = mealsArray2.getJSONObject(i);
                                                    String mealName = meal.getString("strMeal");
                                                    mealsWithIngredient2.add(mealName);
                                                    ingredient2Meals.append(mealName).append(", ");
                                                }

                                                Log.d(TAG, ingredient2Meals.toString());

                                                //calculate intersection
                                                HashSet<String> set1 = new HashSet<>(mealsWithIngredient1);
                                                HashSet<String> set2 = new HashSet<>(mealsWithIngredient2);
                                                set1.retainAll(set2);


                                                Log.d(TAG, set1.toString());
                                                displayMealButtons(new ArrayList<>(set1));

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

                            requestQueue.add(request2);

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

        requestQueue.add(request1);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // check users chosen and update the background color of receipe menu
        fetchUserMealsAndUpdateButtons();
    }
    private void fetchUserMealsAndUpdateButtons() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user1Meal = dataSnapshot.child("user1Meal").getValue(String.class);
                user2Meal = dataSnapshot.child("user2Meal").getValue(String.class);


                updateButtonColors();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user meals", databaseError.toException());
            }
        });
    }

    private void updateButtonColors() {
        // iterate all recipe buttons and change color
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof Button) {
                Button button = (Button) view;
                String mealName = button.getText().toString();

                if (mealName.equals(user1Meal) && mealName.equals(user2Meal)) {
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.place_btn2));
                    if(!activityRegistered){registerActivity(mealName);}// red
                } else if (mealName.equals(user1Meal) || mealName.equals(user2Meal)) {
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.place_btn3)); // green
                } else {
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                }
            }
        }
    }

    private void displayMealButtons(ArrayList<String> meals) {
        if (meals.isEmpty()) {
            Toast.makeText(this, "No matching meals found", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String meal : meals) {
            Button button = new Button(this);
            button.setText(meal);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(Menu2.this, MenuDetail.class);
                intent.putExtra("mealName", meal + "," + kitchenNo); // transfer recipe name
                startActivity(intent);
            });
            layout.addView(button);
        }


        updateButtonColors();
    }


    //check if user1 and user2 make the same choice and rigister booking
    private void registerActivity(String mealName) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user1 = dataSnapshot.child("User1").getValue(String.class);
                user2 = dataSnapshot.child("User2").getValue(String.class);


                if (user1 != null && user2 != null) {
                    // create a map of booking detail and upload under Booking in Database
                    DatabaseReference bookingReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Bookings");


                    Map<String, Object> bookingData = new HashMap<>();
                    bookingData.put("mealName", mealName);
                    bookingData.put("user1", user1);
                    bookingData.put("user2", user2);
                    bookingData.put("roomNumber", kitchenNo);
                    bookingData.put("time", "19:00");
                    bookingData.put("Registertime", System.currentTimeMillis());


                    String bookingId = bookingReference.push().getKey();
                    bookingReference.child(bookingId).setValue(bookingData);


                    activityRegistered = true;
                    Log.d(TAG, "Activity registered successfully.");
                } else {
                    Log.e(TAG, "Error: user1 or user2 is null.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user meals", databaseError.toException());
            }
        });
    }



}

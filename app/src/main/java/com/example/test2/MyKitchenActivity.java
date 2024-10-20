package com.example.test2;

import static android.content.ContentValues.TAG;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

//class where the users drag ingredients and group together
//whether you click public kitchen, private kitchen or my kitchen, will enter this activity.

public class MyKitchenActivity extends AppCompatActivity {
    private static final String TAG = "MyKitchenActivity";
    private FrameLayout dropZone;
    private ArrayList<View> draggedItems = new ArrayList<>();
    private DatabaseReference databaseReference;
    private LinearLayout dynamicIngredientsContainer;
    private String ingredient1;
    private String ingredient2;

    //change the url if you use your own Firebase
    private String databaseUrl = "https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private int kitchenNo = 0;
    private boolean isUser1 = true;

    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_kitchen);


        TextView kitchenTitle = findViewById(R.id.kitchenTitle);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();





        if (currentUser != null) {
            currentUserID = currentUser.getUid();
            //Log.d(TAG, "Current UserID: " + currentUserID);
        } else {
            //Log.d(TAG, "No current user logged in");
        }


        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> cancelCurrentUserIngredients());


        Intent intent = getIntent();
        String fromActivity = intent.getStringExtra("from");
        //identify kitchen number from message receive from previous activities
        if ("Kitchen1".equals(fromActivity)) {
           kitchenTitle.setText("Public Kitchen");
           kitchenNo=1;

        }
        else if ("Kitchen4".equals(fromActivity)) {
            kitchenTitle.setText("Public Kitchen");
            kitchenNo=4;

        }
        else if ("Kitchen3".equals(fromActivity)) {
            kitchenTitle.setText("Public Kitchen");
            kitchenNo=3;

        }
        else if ("Kitchen6".equals(fromActivity)) {
            kitchenTitle.setText("Bob's Kitchen");
            kitchenNo=6;

        }
        else {

            kitchenTitle.setText("My Kitchen");
            kitchenNo=6;
        }

        ShowUserIngredients();

        databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference("ingredients");

        // get the ingredient container
        dynamicIngredientsContainer = findViewById(R.id.dynamic_ingredients_container);


        loadIngredientsFromDatabase();

        dropZone = findViewById(R.id.drop_zone);

        dropZone.setOnClickListener(v -> {
            if (draggedItems.size() == 2) {
                showDialog();
            } else {
                Toast.makeText(MyKitchenActivity.this, "请拖入两个项目", Toast.LENGTH_SHORT).show();
            }
        });



        /*initDraggable(findViewById(R.id.banana), "banana");
        initDraggable(findViewById(R.id.pineapple), "pineapple");
        initDraggable(findViewById(R.id.beef), "beef");*/


        //check the status of dragging the ingredient
        dropZone.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        return true;

                    case DragEvent.ACTION_DROP:
                        String tag = event.getClipData().getItemAt(0).getText().toString();
                        View draggedView = dynamicIngredientsContainer.findViewWithTag(tag);
                        if (draggedView != null) {
                            //Log.d(TAG, "Dropped: " + tag); // Log dropped item
                            handleDrop(draggedView);
                        } else {
                            //Log.e(TAG, "Dragged view is null for tag: " + tag);
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        return true;

                    default:
                        return false;
                }
            }
        });

    }
    private void loadIngredientsFromDatabase() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // remove previous views
                dynamicIngredientsContainer.removeAllViews();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> ingredient = (Map<String, Object>) snapshot.getValue();
                    String name = (String) ingredient.get("name");
                    String color = (String) ingredient.get("color");


                    createIngredientView(name, color);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MyKitchenActivity.this, "Failed to load ingredients.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Dynamic create ingredients view and add into containers
    private void createIngredientView(String name, String color) {
        // create a linear layout to show the ingredients name and image
        LinearLayout ingredientLayout = new LinearLayout(this);
        ingredientLayout.setOrientation(LinearLayout.VERTICAL);
        ingredientLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);


        ImageView imageView = new ImageView(this);
        int imageResource = getResources().getIdentifier(name.toLowerCase(), "drawable", getPackageName());

        // if find the image, use, else use a color as placeholder
        if (imageResource != 0) {
            imageView.setImageResource(imageResource);
        } else {
            imageView.setBackgroundColor(Color.parseColor(color));
        }


        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(200, 200);
        imageView.setLayoutParams(viewParams);

        // create a textview to show the name
        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextSize(16);
        nameView.setGravity(View.TEXT_ALIGNMENT_CENTER);


        ingredientLayout.addView(imageView);
        ingredientLayout.addView(nameView);


        initDraggable(ingredientLayout, name);


        dynamicIngredientsContainer.addView(ingredientLayout);
    }

    // intiate draggable image
    private void initDraggable(View view, String tag) {
        view.setTag(tag);
        view.setOnLongClickListener(v -> {
            ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
            ClipData dragData = new ClipData((CharSequence) v.getTag(),
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(dragData, shadowBuilder, null, 0);
            return true;
        });
    }


    //clone a new image in dropzone and makesure there is no more than 2 images
    private void handleDrop(View draggedView) {
        if (draggedItems.size() < 2) {
            ImageView imageView = new ImageView(this);


            String ingredientName = (String) draggedView.getTag();

            int imageResource = getResources().getIdentifier(ingredientName.toLowerCase(), "drawable", getPackageName());

            if (imageResource != 0) {
                imageView.setImageResource(imageResource);
                //Log.d(TAG, "Loaded image for: " + ingredientName);
            } else {
                imageView.setBackgroundColor(Color.GRAY);
                //Log.e(TAG, "No image found for: " + ingredientName);
            }


            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
            imageView.setLayoutParams(params);



            // update dropzone display
            updateDropZone(ingredientName);
            handleIngredient(ingredientName);

        } else {
            Toast.makeText(this, "only can drop 2 images", Toast.LENGTH_SHORT).show();
        }
    }



    private void updateDropZone(String ingredientName) {
        //judge if there is already 2 images
        if (draggedItems.size() < 2 && ingredientName != null && !ingredientName.isEmpty()) {

            ImageView imageView = new ImageView(this);
            int imageResource = getResources().getIdentifier(ingredientName.toLowerCase(), "drawable", getPackageName());

            if (imageResource != 0) {
                imageView.setImageResource(imageResource);
            } else {
                imageView.setBackgroundColor(Color.parseColor("#FFCC99")); // if cannot find a image, use default color
            }


            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
            params.gravity = android.view.Gravity.CENTER;
            imageView.setLayoutParams(params);


            draggedItems.add(imageView);
            dropZone.addView(imageView);

            // if there is 2 images, display them side by side
            if (draggedItems.size() == 2) {

                rearrangeDropZone();
            }
        }
    }


    private void rearrangeDropZone() {
        dropZone.removeAllViews();
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (View view : draggedItems) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
            params.setMargins(16, 0, 16, 0);
            view.setLayoutParams(params);
            linearLayout.addView(view);
        }

        dropZone.addView(linearLayout);
    }


    //show grouping detail
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyKitchenActivity.this);
        builder.setTitle("Confirm Grouping")
                .setMessage("Bob: " + ingredient1 + "\nAmy: " + ingredient2)
                .setPositiveButton("Agree", (dialog, which) -> {

                    Intent intent = new Intent(MyKitchenActivity.this, Menu2.class);
                    intent.putExtra("ingredients", ingredient1+","+ingredient2+","+kitchenNo);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                    dialog.dismiss();
                });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //display ingredients of other user
    private void ShowUserIngredients() {

        databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Kitchens/" + kitchenNo);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get Ingredient of user1 and user2
                String user1 = dataSnapshot.child("User1").getValue(String.class);
                String user1Ingredients = dataSnapshot.child("user1Ingredients").getValue(String.class);
                String user2 = dataSnapshot.child("User2").getValue(String.class);
                String user2Ingredients = dataSnapshot.child("user2Ingredients").getValue(String.class);// 获取 user1Ingredients
                ingredient1 = user1Ingredients;
                ingredient2 = user2Ingredients;
                Log.d(TAG, user1+" "+""+user1Ingredients+" "+ user2+" "+ user2Ingredients);


                if (user1 != null && !user1.isEmpty() && user1Ingredients != null && !user1Ingredients.isEmpty()) {

                    isUser1=false;
                    updateDropZone(user1Ingredients);
                }

                if (user2 != null && !user2.isEmpty() && user2Ingredients != null && !user2Ingredients.isEmpty()) {

                    updateDropZone(user2Ingredients);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("FirebaseCheck", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void handleIngredient(String ingredientName) {
        //upload the ingredient dropped by the current user
        databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Kitchens/" + kitchenNo);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user1 = dataSnapshot.child("User1").getValue(String.class);
                String user2 = dataSnapshot.child("User2").getValue(String.class);

                if (user1 == null || user1.isEmpty()) {
                    // if user1 is null, current user is user1
                    DatabaseReference user1Ref = dataSnapshot.getRef().child("User1");
                    user1Ref.setValue(currentUserID).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User1 updated with current user's ID");
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "Failed to update User1: " + e.getMessage());
                    });

                    DatabaseReference user1Ingre = dataSnapshot.getRef().child("user1Ingredients");
                    user1Ingre.setValue(ingredientName).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User1 ingredients updated with current user's selection");
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "Failed to update User1 ingredients: " + e.getMessage());
                    });
                } else if (user2 == null || user2.isEmpty()) {
                    // else if user2 is null, current user is user2
                    DatabaseReference user2Ref = dataSnapshot.getRef().child("User2");
                    user2Ref.setValue(currentUserID).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User2 updated with current user's ID");
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "Failed to update User2: " + e.getMessage());
                    });
                    ingredient2=ingredientName;
                    DatabaseReference user2Ingre = dataSnapshot.getRef().child("user2Ingredients");
                    user2Ingre.setValue(ingredientName).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User2 ingredients updated with current user's selection");
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "Failed to update User2 ingredients: " + e.getMessage());
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("FirebaseCheck", "Failed to read value.", databaseError.toException());
            }
        });
    }


    //cancel groupping
    private void cancelCurrentUserIngredients(){

        databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Kitchens/" + kitchenNo);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user1 = dataSnapshot.child("User1").getValue(String.class);
                String user2 = dataSnapshot.child("User2").getValue(String.class);

                //if current user is user1, clear the imformation of user1 and user2
                if (user1.equals(currentUserID)) {

                    resetUser1AndUser2(dataSnapshot.getRef());
                } else if (user2.equals(currentUserID)) {
                    //if current user is user2, clear the imformation of user2
                    resetUser2(dataSnapshot.getRef());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("FirebaseCheck", "Failed to read value.", databaseError.toException());
            }
        });
    }


    private void resetUser1AndUser2(DatabaseReference ref) {
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("User1", "");
        emptyData.put("user1Ingredients", "");
        emptyData.put("User2", "");
        emptyData.put("user2Ingredients", "");

        ref.updateChildren(emptyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MyKitchenActivity.this, "User1 and User2 data reset", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyKitchenActivity.this, "Failed to reset data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void resetUser2(DatabaseReference ref) {
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("User2", "");
        emptyData.put("user2Ingredients", "");

        ref.updateChildren(emptyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MyKitchenActivity.this, "User2 data reset", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyKitchenActivity.this, "Failed to reset data", Toast.LENGTH_SHORT).show();
            }
        });
    }


}







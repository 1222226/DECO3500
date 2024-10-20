package com.example.test2;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

//import com.example.test2.Database.Ingredients;
import com.example.test2.Database.Grouping;

//import com.example.test2.Database.Groupping;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNavigationView;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d(TAG, "onCreate executed");
        FirebaseApp.initializeApp(this);
        ///Log.d(TAG, "Firebase initialized");

        //bind bottom navigate buttons
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_basket) {
                Intent intent = new Intent(MainActivity.this, Booking.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_recipes) {
                Intent intent = new Intent(MainActivity.this, Booking.class);
                startActivity(intent);

                return true;
            } else if (id == R.id.nav_community) {
                Intent intent = new Intent(MainActivity.this, Forum.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_login) {
                Log.d(TAG, "Login selected");
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });




        // Initiate and upload Indredients
        //Uncomment the following 2 lines if you set a new Firebase and run the first time
       //Ingredients ingredients = new Ingredients();
        //ingredients.addIngredients();


        // Initiate and upload Kitchens
        //Uncomment the following 2 lines if you set a new Firebase and run the first time
        //Grouping grouping = new Grouping();
        //grouping.addKitchens();


        CardView buttonToB = findViewById(R.id.button_to_b);
        buttonToB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PublicKitchenActivity.class);
                startActivity(intent);
            }
        });

        // Button to navigate to Activity C
        CardView buttonToC = findViewById(R.id.button_to_c);
        buttonToC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PrivateKitchen.class);
                startActivity(intent);
            }
        });

        // Button to navigate to Activity D
        CardView buttonToD = findViewById(R.id.button_to_d);
        buttonToD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyKitchenActivity.class);
                startActivity(intent);
            }
        });
    }
}
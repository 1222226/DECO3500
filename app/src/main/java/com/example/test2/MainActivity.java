package com.example.test2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.example.test2.Database.Ingredients;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        // 初始化 IngredientManager 并执行上传食材的操作
       //Ingredients ingredients = new Ingredients();
        //ingredients.addIngredients();
        // Button to navigate to Activity B
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
                Intent intent = new Intent(MainActivity.this, PrivateKitchenActivity.class);
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
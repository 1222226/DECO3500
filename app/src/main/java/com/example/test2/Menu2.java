package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Menu2 extends AppCompatActivity {

    private Button pinaaple_detail;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu2);

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
}
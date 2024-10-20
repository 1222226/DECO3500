package com.example.test2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
//class to choose the spot of the public kitchen
public class PublicKitchenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_kitchen);


        Button empty1 = findViewById(R.id.buttonEmpty1);
        Button empty2 = findViewById(R.id.buttonEmpty2);
        Button place1 = findViewById(R.id.buttonPlace1);




        empty1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublicKitchenActivity.this, MyKitchenActivity.class);
                //send a code to locate kitchen No
                intent.putExtra("from", "Kitchen1");
                startActivity(intent);
            }
        });

        empty2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublicKitchenActivity.this, MyKitchenActivity.class);
                intent.putExtra("from", "Kitchen4");
                startActivity(intent);
            }
        });

        place1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublicKitchenActivity.this, MyKitchenActivity.class);
                intent.putExtra("from", "Kitchen3");
                startActivity(intent);
            }
        });


    }
}

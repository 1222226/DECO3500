package com.example.test2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
//the activity list the booking information
public class Booking extends AppCompatActivity {

    private TextView kitchenNoTextView, timeTextView, user1TextView, user2TextView;
    private Button mealButton;
    private Button cancelButton;
    private Button startTimerButton;

    private DatabaseReference bookingReference;
    private String currentUserID;
    private String mealName;
    private int kitchenNo;
    private static final String TAG = "BookingActivity";
    private String databaseUrl = "https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        kitchenNoTextView = findViewById(R.id.kitchen_no);
        timeTextView = findViewById(R.id.booking_time);
        user1TextView = findViewById(R.id.user1);
        user2TextView = findViewById(R.id.user2);
        mealButton = findViewById(R.id.meal_button);
        startTimerButton=findViewById(R.id.startTimer_button);
        cancelButton=findViewById(R.id.cancel_button);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        }

        bookingReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Bookings");


        loadLatestBookingForUser();

        // jump to recipe detail
        mealButton.setOnClickListener(v -> {
            if (mealName != null) {
                Intent intent = new Intent(Booking.this, MenuDetail.class);
                intent.putExtra("mealName", mealName + "," + kitchenNo+","+0); // 传递菜品名称和厨房号
                startActivity(intent);
            } else {
                Toast.makeText(Booking.this, "No meal selected", Toast.LENGTH_SHORT).show();
            }
        });

        startTimerButton.setOnClickListener(v -> {

                Intent intent = new Intent(Booking.this, TimerActivity.class);
                intent.putExtra("mealName", mealName + "," + kitchenNo+","+0); // 传递菜品名称和厨房号
                startActivity(intent);

        });


        cancelButton.setOnClickListener(v -> {
            // get the newest booking of the current user
            bookingReference.orderByChild("Registertime").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String user1 = snapshot.child("user1").getValue(String.class);
                        String user2 = snapshot.child("user2").getValue(String.class);
                        String bookingId = snapshot.getKey();
                        DatabaseReference kitchenReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Kitchens/" + kitchenNo);


                        if (user1.equals(currentUserID) || user2.equals(currentUserID)) {
                            // clear Kitchen data
                            Map<String, Object> emptyData = new HashMap<>();
                            emptyData.put("User1", "");
                            emptyData.put("User2", "");
                            emptyData.put("user1Ingredients", "");
                            emptyData.put("user2Ingredients", "");
                            emptyData.put("user1Meal", "");
                            emptyData.put("user2Meal", "");

                            kitchenReference.updateChildren(emptyData).addOnCompleteListener(kitchenTask -> {
                                if (kitchenTask.isSuccessful()) {
                                    Toast.makeText(Booking.this, "Kitchen data cleared", Toast.LENGTH_SHORT).show();

                                    finish();
                                } else {
                                    Toast.makeText(Booking.this, "Failed to clear kitchen data", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.d(TAG, "No booking found for current user");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching bookings", databaseError.toException());
                }
            });
        });


    }

    // get the newest booking of the current user
    private void loadLatestBookingForUser() {
        bookingReference.orderByChild("Registertime").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String user1 = snapshot.child("user1").getValue(String.class);
                    String user2 = snapshot.child("user2").getValue(String.class);
                    kitchenNo = snapshot.child("roomNumber").getValue(Integer.class);
                    String time = snapshot.child("time").getValue(String.class);
                    mealName = snapshot.child("mealName").getValue(String.class);


                    if (user1.equals(currentUserID) || user2.equals(currentUserID)) {

                        kitchenNoTextView.setText("Kitchen No: " + kitchenNo);
                        timeTextView.setText("Time: " + time);
                        user1TextView.setText("User 1: " + user1);
                        user2TextView.setText("User 2: " + user2);
                        mealButton.setText("Meal: " + mealName);
                    } else {
                        Log.d(TAG, "No booking found for current user");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching bookings", databaseError.toException());
            }
        });
    }
}

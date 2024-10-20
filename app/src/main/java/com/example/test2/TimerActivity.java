package com.example.test2;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
//an Timer, to simulate the cooking guide
public class TimerActivity extends AppCompatActivity {
    private TextView timerText;
    private static final long START_TIME_IN_MILLIS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);

        timerText = findViewById(R.id.timerText);
        new CountDownTimer(START_TIME_IN_MILLIS, 1000) {

            public void onTick(long millisUntilFinished) {
                timerText.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {

                Intent intent = new Intent(TimerActivity.this, StepCompletedActivity.class);
                startActivity(intent);
                finish();
            }

        }.start();
    }
}
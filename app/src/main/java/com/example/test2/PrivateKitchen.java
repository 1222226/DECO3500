package com.example.test2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//if you want to enter others private kitchen, scan the QR code provided by the owner
//a class using ZXing to build scanner
public class PrivateKitchen extends AppCompatActivity {
    private Button scanButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_private_kitchen);
        scanButton = findViewById(R.id.scan_button);


        scanButton.setOnClickListener(v -> {
            new IntentIntegrator(PrivateKitchen.this)
                    .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                    .setPrompt("Scan a QR Code")

                    .setCameraId(0)
                    .setBeepEnabled(false)
                    .setBarcodeImageEnabled(true)
                    .setOrientationLocked(true)
                    .initiateScan();
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {

                Intent intent = new Intent(PrivateKitchen.this, MyKitchenActivity.class);
                intent.putExtra("from", "Kitchen6");
                startActivity(intent);
            } else {
                Toast.makeText(this, "No QR code found", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
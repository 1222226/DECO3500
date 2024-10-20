package com.example.test2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;
//the forum activity
//we upload the image in the Firebase Storage, and use a url in Database to link to it.
public class Forum extends AppCompatActivity {

    private LinearLayout postContainer;
    private Button createPostButton, submitPostButton;
    private EditText titleEditText, contentEditText;
    private ImageView imageView;
    private Uri imageUri;

    private DatabaseReference postsReference;
    private StorageReference storageReference;

    private String currentUserID;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ForumActivity";

    private String databaseUrl = "https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        postContainer = findViewById(R.id.post_container);
        createPostButton = findViewById(R.id.create_post_button);
        submitPostButton = findViewById(R.id.submit_post_button);
        titleEditText = findViewById(R.id.title_edittext);
        contentEditText = findViewById(R.id.content_edittext);
        imageView = findViewById(R.id.post_image);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        }

        postsReference = FirebaseDatabase.getInstance(databaseUrl).getReference("Posts");
        storageReference = FirebaseStorage.getInstance().getReference("PostImages");


        loadPosts();

        createPostButton.setOnClickListener(v -> openImageSelector());


        submitPostButton.setOnClickListener(v -> uploadPost());
    }

    // Open album
    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadPost() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Title, content, and image are required", Toast.LENGTH_SHORT).show();
            return;
        }


        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                String imageUrl = uri.toString();
                savePostToDatabase(title, content, imageUrl);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(Forum.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    // save the post information to database
    private void savePostToDatabase(String title, String content, String imageUrl) {
        String postId = postsReference.push().getKey();
        Map<String, Object> postData = new HashMap<>();
        postData.put("userID", currentUserID);
        postData.put("title", title);
        postData.put("content", content);
        postData.put("imageUrl", imageUrl);
        postData.put("likes", 0);

        postsReference.child(postId).setValue(postData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Forum.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                loadPosts();
            } else {
                Toast.makeText(Forum.this, "Failed to upload post", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPosts() {
        postsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postContainer.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userID = snapshot.child("userID").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    int likes = snapshot.child("likes").getValue(Integer.class);
                    String postId = snapshot.getKey();

                    View postView = getLayoutInflater().inflate(R.layout.post_item, postContainer, false);
                    TextView userIdView = postView.findViewById(R.id.post_userId);
                    TextView titleView = postView.findViewById(R.id.post_title);
                    TextView contentView = postView.findViewById(R.id.post_content);
                    ImageView postImageView = postView.findViewById(R.id.post_image);
                    TextView likesView = postView.findViewById(R.id.post_likes);
                    Button likeButton = postView.findViewById(R.id.like_button);

                    titleView.setText(title);
                    contentView.setText(content);
                    likesView.setText(String.valueOf(likes) + " Likes");

                    // Use Glide to load images
                    Glide.with(Forum.this).load(imageUrl).into(postImageView);


                    likeButton.setOnClickListener(v -> {
                        int newLikes = likes + 1;
                        postsReference.child(postId).child("likes").setValue(newLikes);
                        likesView.setText(newLikes + " Likes");
                    });

                    postContainer.addView(postView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading posts", databaseError.toException());
            }
        });}

        // check request to read the phone storage
        private void checkAndRequestPermissions() {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {

                openImageSelector();
            }
        }


}

package com.example.test2;

import static android.content.ContentValues.TAG;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;


public class MyKitchenActivity extends AppCompatActivity {

    private FrameLayout dropZone;
    private ArrayList<View> draggedItems = new ArrayList<>();
    private DatabaseReference databaseReference;
    private LinearLayout dynamicIngredientsContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_kitchen);

        // 初始化 Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance("https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("ingredients");

        // 获取布局中用于存放食材的 LinearLayout 容器
        dynamicIngredientsContainer = findViewById(R.id.dynamic_ingredients_container);

        // 从数据库加载食材
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
                        View draggedView = dynamicIngredientsContainer.findViewWithTag(tag); // 通过 tag 查找视图
                        if (draggedView != null) {
                            Log.d(TAG, "Dropped: " + tag); // Log dropped item
                            handleDrop(draggedView);
                        } else {
                            Log.e(TAG, "Dragged view is null for tag: " + tag); // 如果没有找到 view，记录错误
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
        // 监听数据变化
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 清空之前的视图
                dynamicIngredientsContainer.removeAllViews();

                // 遍历数据库中的食材
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> ingredient = (Map<String, Object>) snapshot.getValue();
                    String name = (String) ingredient.get("name");
                    String color = (String) ingredient.get("color");

                    // 动态创建 View 并添加到布局中
                    createIngredientView(name, color);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 处理数据库访问错误
                Toast.makeText(MyKitchenActivity.this, "Failed to load ingredients.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 动态创建食材视图并添加到容器中
    private void createIngredientView(String name, String color) {
        // 创建一个 LinearLayout 来包含色块和名称
        LinearLayout ingredientLayout = new LinearLayout(this);
        ingredientLayout.setOrientation(LinearLayout.VERTICAL);
        ingredientLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);

        // 创建显示食材图片的 ImageView
        ImageView imageView = new ImageView(this);
        int imageResource = getResources().getIdentifier(name.toLowerCase(), "drawable", getPackageName());

        // 如果找到资源图片，则设置图片；否则使用背景颜色作为占位符
        if (imageResource != 0) {
            imageView.setImageResource(imageResource); // 加载图片
        } else {
            imageView.setBackgroundColor(Color.parseColor(color)); // 以颜色代替图片
        }

        // 设置图片大小
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(200, 200);
        imageView.setLayoutParams(viewParams);

        // 创建 TextView 显示食材名称
        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextSize(16);
        nameView.setGravity(View.TEXT_ALIGNMENT_CENTER);

        // 将图片和名称添加到 LinearLayout 中
        ingredientLayout.addView(imageView);
        ingredientLayout.addView(nameView);

        // 设置为可拖拽
        initDraggable(ingredientLayout, name);

        // 将 LinearLayout 添加到容器中
        dynamicIngredientsContainer.addView(ingredientLayout);
    }

    // 初始化可拖动的色块
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


    private void handleDrop(View draggedView) {
        if (draggedItems.size() < 2) {
            ImageView imageView = new ImageView(this);

            // 从 draggedView 的 tag 中获取食材名称
            String ingredientName = (String) draggedView.getTag();

            // 根据食材名称从 drawable 加载图片
            int imageResource = getResources().getIdentifier(ingredientName.toLowerCase(), "drawable", getPackageName());

            if (imageResource != 0) {
                imageView.setImageResource(imageResource); // 设置图片
                Log.d(TAG, "Loaded image for: " + ingredientName);
            } else {
                imageView.setBackgroundColor(Color.GRAY); // 如果找不到图片，用灰色占位
                Log.e(TAG, "No image found for: " + ingredientName);
            }

            // 设置图片的布局参数
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
            imageView.setLayoutParams(params);

            // 将图片添加到 draggedItems 列表
            draggedItems.add(imageView);

            // 更新 dropZone 的显示
            updateDropZone();
        } else {
            Toast.makeText(this, "只能拖入两个视图", Toast.LENGTH_SHORT).show();
        }
    }



    private void updateDropZone() {
        dropZone.removeAllViews();

        if (draggedItems.size() == 1) {
            // 如果只有一个视图，居中显示
            View view = draggedItems.get(0);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
            params.gravity = android.view.Gravity.CENTER;
            view.setLayoutParams(params);
            dropZone.addView(view);
            Log.d(TAG, "Added single view to drop zone");

        } else if (draggedItems.size() == 2) {
            // 如果有两个视图，并排显示
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (View view : draggedItems) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
                params.setMargins(16, 0, 16, 0);
                view.setLayoutParams(params);
                linearLayout.addView(view);
                Log.d(TAG, "Added view to linear layout");
            }

            dropZone.addView(linearLayout);
            Log.d(TAG, "Added linear layout to drop zone");
        }
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyKitchenActivity.this);
        builder.setTitle("Confirm Grouping")
                .setMessage("You: Beef \nAmy: Pineapple")
                .setPositiveButton("Agree", (dialog, which) -> {
                    // 跳转到下一个Activity
                    Intent intent = new Intent(MyKitchenActivity.this, Menu2.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // 关闭对话框
                    dialog.dismiss();
                });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


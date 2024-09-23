package com.example.test2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

/*public class MyKitchenActivity extends AppCompatActivity {

    private FrameLayout dropZone;
    private LinearLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_kitchen);

        dropZone = findViewById(R.id.drop_zone);

        // Initialize draggable views
        initDraggable(findViewById(R.id.banana), "block1");
        initDraggable(findViewById(R.id.pineapple), "block2");
        initDraggable(findViewById(R.id.beef), "block3");

        dropZone.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                    case DragEvent.ACTION_DROP:
                        View draggedView = (View) event.getLocalState();
                        handleDrop(draggedView);
                        return true;
                }
                return true;
            }
        });
    }

    private void initDraggable(View view, String tag) {
        view.setTag(tag);
        view.setOnLongClickListener(v -> {
            ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
            ClipData dragData = new ClipData((CharSequence) v.getTag(),
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            // Pass the view as localState
            v.startDragAndDrop(dragData, shadowBuilder, v, 0);
            return true;
        });
    }

    private void handleDrop(View draggedView) {
        // Remove the view from its previous parent
        ViewGroup owner = (ViewGroup) draggedView.getParent();
        if (owner != null) {
            owner.removeView(draggedView);
        }
        // Set new layout parameters if needed
        draggedView.setLayoutParams(new FrameLayout.LayoutParams(100, 100));
        // Add the view to the drop zone
        dropZone.addView(draggedView);
    }
}*/


import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MyKitchenActivity extends AppCompatActivity {

    private FrameLayout dropZone;
    private ArrayList<View> draggedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_kitchen);

        dropZone = findViewById(R.id.drop_zone);


        initDraggable(findViewById(R.id.banana), "banana");
        initDraggable(findViewById(R.id.pineapple), "pineapple");
        initDraggable(findViewById(R.id.beef), "beef");


        dropZone.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // 确保拖动开始时视图可拖动
                        return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                    case DragEvent.ACTION_DRAG_ENTERED:
                        // 拖动到目标区域时
                        v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        // 拖出目标区域时
                        v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        return true;

                    case DragEvent.ACTION_DROP:
                        // 获取被拖动的视图
                        String tag = event.getClipData().getItemAt(0).getText().toString();
                        View draggedView = findViewById(getResources().getIdentifier(tag, "id", getPackageName()));
                        if (draggedView != null) {
                            handleDrop(draggedView);
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        // 恢复背景颜色
                        v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        return true;

                    default:
                        return false;
                }
            }
        });
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
            // 将拖动的视图克隆并加入列表
            View clone = new View(this);
            clone.setLayoutParams(new FrameLayout.LayoutParams(200, 200));
            clone.setBackground(draggedView.getBackground());
            draggedItems.add(clone);

            updateDropZone();
        } else {
            Toast.makeText(this, "只能拖入两个视图", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateDropZone() {
        dropZone.removeAllViews();

        if (draggedItems.size() == 1) {
            // 一个视图时，居中显示
            View view = draggedItems.get(0);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
            params.gravity = android.view.Gravity.CENTER;
            view.setLayoutParams(params);
            dropZone.addView(view);
        } else if (draggedItems.size() == 2) {
            // 两个视图时，并排显示
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
    }
}


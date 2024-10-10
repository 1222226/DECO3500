package com.example.test2.Database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Ingredients {

    private DatabaseReference databaseReference;

    public Ingredients() {
        // 初始化 Firebase Realtime Database 引用
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://deco-3500test-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = database.getReference("ingredients");
    }

    // 添加一批食材到数据库
    public void addIngredients() {
        // 食材列表和对应的颜色代码
        String[][] ingredients = {
                {"Chicken", "#F8E5C3"},
                {"Salmon", "#FF8C69"},
                {"Beef", "#A52A2A"},
                {"Pork", "#FFC0CB"},
                {"Avocado", "#568203"},
                {"Bacon", "#D2691E"},
                {"Bread", "#F5DEB3"},
                {"Cheese", "#FFD700"},
                {"Rice", "#FFFFF0"},
                {"Egg", "#FFFACD"},
                {"Tomatoes", "#FF6347"},
                {"Potatoes", "#D2B48C"},
                {"Oats", "#EEDC82"},
                {"Tuna", "#8A9A5B"},
                {"Egg Plants", "#9370DB"}
        };

        // 遍历食材列表并将每个食材存储到数据库中
        for (String[] ingredient : ingredients) {
            String name = ingredient[0];
            String color = ingredient[1];
            saveIngredientToDatabase(name, color, "");
        }
    }

    // 保存食材信息到 Realtime Database
    private void saveIngredientToDatabase(String name, String color, String imageUrl) {
        Map<String, Object> ingredient = new HashMap<>();
        ingredient.put("name", name);
        ingredient.put("color", color);
        ingredient.put("image_url", imageUrl);  // 暂时为空

        // 将数据存储在以食材名字命名的子节点下
        databaseReference.child(name).setValue(ingredient)
                .addOnSuccessListener(aVoid -> {
                    // 成功写入数据
                    System.out.println("Ingredient " + name + " added successfully.");
                })
                .addOnFailureListener(e -> {
                    // 处理数据写入失败的情况
                    System.err.println("Error adding ingredient " + name);
                });
    }
}

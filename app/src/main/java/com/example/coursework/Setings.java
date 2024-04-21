package com.example.coursework;

import  android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Setings extends AppCompatActivity {
    private DatabaseReference lecturesRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);
        bottomNavigationView.setSelectedItemId(R.id.menu_lectures);


        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemid = item.getItemId();

            if (itemid == R.id.menu_profile){
                startActivity(new Intent(Setings.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                return true;
            }else if (itemid == R.id.menu_lectures){
                return true;
            }else if (itemid == R.id.menu_leson) {
                startActivity(new Intent(Setings.this, Leson.class));
                overridePendingTransition(R.anim.slied_in_left,R.anim.slide_out_right);
                return true;
            }
            return false;
        });

        // Получаем ссылку на базу данных Firebase
        lecturesRef = FirebaseDatabase.getInstance().getReference().child("lectures");

        // Находим кнопки
        ImageButton lectureButton1 = findViewById(R.id.imageButton1);
        ImageButton lectureButton2 = findViewById(R.id.imageButton2);
        ImageButton lectureButton3 = findViewById(R.id.imageButton3);
        ImageButton lectureButton4 = findViewById(R.id.imageButton4);
        ImageButton lectureButton5 = findViewById(R.id.imageButton5);
        ImageButton lectureButton6 = findViewById(R.id.imageButton6);

        // Назначаем обработчик нажатия на кнопки
        lectureButton1.setOnClickListener(v -> openLecture("lecture1"));
        lectureButton2.setOnClickListener(v -> openLecture("lecture2"));
        lectureButton3.setOnClickListener(v -> openLecture("lecture3"));
        lectureButton4.setOnClickListener(v -> openLecture("lecture4"));
        lectureButton5.setOnClickListener(v -> openLecture("lecture5"));
        lectureButton6.setOnClickListener(v -> openLecture("lecture6"));
    }

    private void openLecture(String lectureKey) {
        // Запускаем ActivityLectures и передаем ключ лекции
        Intent intent = new Intent(Setings.this, lectures.class);
        intent.putExtra("lectureKey", lectureKey);
        startActivity(intent);
    }
    }

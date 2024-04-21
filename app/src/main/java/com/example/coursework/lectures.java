package com.example.coursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class lectures extends AppCompatActivity {

    private DatabaseReference lecturesRef;
    private TextView lectureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);



        // Получаем ссылку на базу данных Firebase
        lecturesRef = FirebaseDatabase.getInstance().getReference().child("lectures");

        // Находим TextView для отображения лекции
        lectureTextView = findViewById(R.id.textViewLectures);

        // Получаем ключ лекции из Intent
        String lectureKey = getIntent().getStringExtra("lectureKey");

        // Загружаем лекцию из базы данных
        loadLecture(lectureKey);


    }

    private void loadLecture(String lectureKey) {
        lecturesRef.child(lectureKey).child("text").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Получаем текст лекции и устанавливаем его в TextView
                String lectureText = dataSnapshot.getValue(String.class);
                lectureText = lectureText.replace("\\n", "\n");
                lectureTextView.setText(lectureText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при загрузке данных
                Toast.makeText(lectures.this, "Ошибка загрузки лекции", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

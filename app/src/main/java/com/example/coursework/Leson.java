package com.example.coursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Leson extends AppCompatActivity {

    private DatabaseReference lecturesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leson);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);
        bottomNavigationView.setSelectedItemId(R.id.menu_lectures);
        bottomNavigationView.setSelectedItemId(R.id.menu_leson);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemid = item.getItemId();

            if (itemid == R.id.menu_profile){
                startActivity(new Intent(Leson.this, MainActivity.class));
                overridePendingTransition(R.anim.slied_in_left,R.anim.slide_out_right);
            }else if (itemid == R.id.menu_lectures){
                startActivity(new Intent(Leson.this, Setings.class));
                overridePendingTransition(R.anim.slied_in_left,R.anim.slide_out_right);
                return true;
            }else if (itemid == R.id.menu_leson) {
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

        // Устанавливаем обработчик нажатия на кнопки
        lectureButton1.setOnClickListener(v -> openLecture("lecture1", lectureButton1));
        lectureButton2.setOnClickListener(v -> openLecture("lecture2", lectureButton2));
        lectureButton3.setOnClickListener(v -> openLecture("lecture3", lectureButton3));
        lectureButton4.setOnClickListener(v -> openLecture("lecture4", lectureButton4));
        lectureButton5.setOnClickListener(v -> openLecture("lecture5", lectureButton5));
        lectureButton6.setOnClickListener(v -> openLecture("lecture6", lectureButton6));

        // Загружаем состояние лекций
        loadLectureState(lectureButton1, "lecture1");
        loadLectureState(lectureButton2, "lecture2");
        loadLectureState(lectureButton3, "lecture3");
        loadLectureState(lectureButton4, "lecture4");
        loadLectureState(lectureButton5, "lecture5");
        loadLectureState(lectureButton6, "lecture6");
    }

    private void openLecture(String lectureKey, ImageButton button) {
        String userId = getCurrentUserId(); // Получаем идентификатор текущего пользователя

        // Проверяем состояние прочтения лекции для текущего пользователя
        lecturesRef.child("users").child(userId).child(lectureKey).child("isRead").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isRead = dataSnapshot.getValue(Boolean.class);
                if (isRead != null && isRead) {
                    // Если лекция уже прочитана, переходим на активность с заданиями
                    Intent intent = new Intent(Leson.this, tasks.class);
                    intent.putExtra("lectureKey", lectureKey);
                    startActivity(intent);
                } else {
                    // Помечаем лекцию как прочитанную для текущего пользователя и обновляем информацию в базе данных
                    lecturesRef.child("users").child(userId).child(lectureKey).child("isRead").setValue(true);
                    // Устанавливаем фон кнопки
                    button.setBackgroundColor(Color.parseColor("#00FF00")); // Устанавливаем цвет фона как в вашей базе данных
                    // Переходим на ActivityLectures
                    Intent intent = new Intent(Leson.this, lectures.class);
                    intent.putExtra("lectureKey", lectureKey);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при загрузке данных
                Toast.makeText(Leson.this, "Ошибка загрузки состояния лекции", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadLectureState(ImageButton button, String lectureKey) {
        String userId = getCurrentUserId(); // Получаем идентификатор текущего пользователя

        // Загружаем состояние лекции для текущего пользователя
        lecturesRef.child("users").child(userId).child(lectureKey).child("isRead").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isRead = dataSnapshot.getValue(Boolean.class);
                if (isRead != null && isRead) {
                    // Если лекция прочитана, устанавливаем фон кнопки
                    button.setBackgroundColor(Color.parseColor("#00FF00")); // Устанавливаем цвет фона как в вашей базе данных
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при загрузке данных
                Toast.makeText(Leson.this, "Ошибка загрузки состояния лекции", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Пример метода для получения идентификатора текущего пользователя
    private String getCurrentUserId() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Если пользователь не аутентифицирован, верните соответствующее значение
            return "user_not_authenticated";
        }
    }
}

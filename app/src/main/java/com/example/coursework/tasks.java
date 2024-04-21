package com.example.coursework;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class tasks extends AppCompatActivity {

    private DatabaseReference tasksRef;
    private TextView questionTextView;
    private Button[] optionButtons;
    private int currentQuestionIndex = 0;

    private DatabaseReference lecturesRef;
    private String currentLectureKey;
    private int completedTasks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Получаем ссылку на базу данных Firebase
        tasksRef = FirebaseDatabase.getInstance().getReference().child("lectures").child(getIntent().getStringExtra("lectureKey")).child("tasks");

        // Инициализируем элементы пользовательского интерфейса
        questionTextView = findViewById(R.id.questionTextView);
        optionButtons = new Button[]{
                findViewById(R.id.optionButton1),
                findViewById(R.id.optionButton2),
                findViewById(R.id.optionButton3),
                findViewById(R.id.optionButton4),
                findViewById(R.id.optionButton5),
                findViewById(R.id.optionButton6)
        };
        currentLectureKey = getIntent().getStringExtra("lectureKey"); // Получаем ключ текущей лекции
        lecturesRef = FirebaseDatabase.getInstance().getReference().child("lectures").child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Загружаем первое задание
        loadNextQuestion();

        // Устанавливаем обработчики нажатия на кнопки с вариантами ответов
        for (int i = 0; i < optionButtons.length; i++) {
            final int optionIndex = i;
            optionButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer(optionIndex);
                }
            });
        }
    }

    private void loadNextQuestion() {
        tasksRef.child("task" + (currentQuestionIndex + 1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String question = dataSnapshot.child("question").getValue(String.class);
                    ArrayList<String> options = new ArrayList<>();
                    for (DataSnapshot optionSnapshot : dataSnapshot.child("options").getChildren()) {
                        String optionText = optionSnapshot.getValue(String.class);
                        options.add(optionText);
                    }

                    // Отображаем вопрос и варианты ответов
                    questionTextView.setText(question);
                    for (int i = 0; i < optionButtons.length; i++) {
                        optionButtons[i].setText(options.get(i));
                    }
                } else {
                    // Если задания закончились, показываем сообщение об этом
                    Toast.makeText(tasks.this, "Вы ответили на все вопросы!", Toast.LENGTH_SHORT).show();
                    // Можно добавить дополнительную логику или переход на следующий экран
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при загрузке данных
                Toast.makeText(tasks.this, "Ошибка загрузки задания", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkAnswer(int optionIndex) {
        tasksRef.child("task" + (currentQuestionIndex + 1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int correctAnswer = dataSnapshot.child("correctAnswer").getValue(Integer.class);
                if (correctAnswer == optionIndex) {
                    // Правильный ответ
                    Toast.makeText(tasks.this, "Правильный ответ!", Toast.LENGTH_SHORT).show();
                    currentQuestionIndex++; // Переходим к следующему вопросу
                    loadNextQuestion();
                    completedTasks++; // Увеличиваем счетчик выполненных заданий
                    if (completedTasks == 6) {
                        setLectureComplete(); // Вызываем метод, который установит complete в true для текущей лекции
                    }
                } else {
                    // Неправильный ответ
                    Toast.makeText(tasks.this, "Неправильный ответ. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при загрузке данных
                Toast.makeText(tasks.this, "Ошибка загрузки ответа", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setLectureComplete() {
        lecturesRef.child(currentLectureKey).child("complete").setValue(true)
                .addOnSuccessListener(aVoid -> Toast.makeText(tasks.this, "задания завершины", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(tasks.this, "Ошибка при установке статуса завершения лекции", Toast.LENGTH_SHORT).show());
    }
}

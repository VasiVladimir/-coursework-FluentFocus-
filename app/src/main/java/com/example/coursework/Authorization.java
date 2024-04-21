package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authorization extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        mAuth = FirebaseAuth.getInstance();


        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Если пользователь уже авторизован, перенаправляем его на главный экран
            startActivity(new Intent(Authorization.this, MainActivity.class));
            finish(); // Закрываем текущую активность, чтобы пользователь не мог вернуться назад
        }

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Проверяем, что поля электронной почты и пароля не пустые
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    // Если хотя бы одно из полей пустое, показываем сообщение об ошибке
                    Toast.makeText(Authorization.this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    // Поля заполнены, пытаемся выполнить вход
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Authorization.this, task -> {
                                if (task.isSuccessful()) {
                                    // Вход выполнен успешно, перенаправляем пользователя
                                    startActivity(new Intent(Authorization.this, MainActivity.class));
                                    overridePendingTransition(R.anim.slied_in_left, R.anim.slide_out_right);
                                    finish();
                                } else {
                                    // Вход не выполнен, показываем сообщение об ошибке
                                    Toast.makeText(Authorization.this, "Неправильный логин или пароль.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Authorization.this, Register.class));
                overridePendingTransition(R.anim.slied_in_left,R.anim.slide_out_right);
            }
        });
    }

}

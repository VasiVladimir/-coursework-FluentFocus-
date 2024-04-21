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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextNickname = findViewById(R.id.editTextNickname);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String nickname = editTextNickname.getText().toString().trim();

                // Проверяем, что все поля заполнены
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(nickname)) {
                    // Если хотя бы одно из полей пустое, показываем сообщение об ошибке
                    Toast.makeText(Register.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    // Все поля заполнены, пытаемся зарегистрировать пользователя
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // После успешной регистрации добавляем данные пользователя в базу данных
                                        mDatabase.child("users").child(user.getUid()).child("nickname").setValue(nickname);
                                    }

                                    // Перенаправляем пользователя на экран авторизации
                                    startActivity(new Intent(Register.this, Authorization.class));
                                    overridePendingTransition(R.anim.slied_in_left, R.anim.slide_out_right);
                                    finish();
                                } else {
                                    // В случае ошибки выводим сообщение об ошибке
                                    Toast.makeText(Register.this, "Ошибка регистрации.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}
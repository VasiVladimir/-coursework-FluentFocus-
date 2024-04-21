package com.example.coursework;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewAvatar;
    private TextView textViewNickname, lecred, Taskcompled;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.menu_profile) {
                return true;
            } else if (itemid == R.id.menu_lectures) {
                startActivity(new Intent(MainActivity.this, Setings.class));
                overridePendingTransition(R.anim.slied_in_left, R.anim.slide_out_right);
                return true;
            } else if (itemid == R.id.menu_leson) {
                startActivity(new Intent(MainActivity.this, Leson.class));
                overridePendingTransition(R.anim.slied_in_left, R.anim.slide_out_right);
                return true;
            }
            return false;
        });

        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        textViewNickname = findViewById(R.id.textViewNickname);
        lecred = findViewById(R.id.textView2);
        Taskcompled = findViewById(R.id.textView3);

        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(v -> openGallery());

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> logout());

        loadNickname();
        loadAvatar();
        countReadLectures();
        countCompletedTasks();
    }

    private void countReadLectures() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("lectures").child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 0;
                    for (DataSnapshot lectureSnapshot : dataSnapshot.getChildren()) {
                        // Проверяем, содержит ли каждый child значение isRead: true
                        if (lectureSnapshot.hasChild("isRead") && lectureSnapshot.child("isRead").getValue(Boolean.class)) {
                            count++;
                        }
                    }
                    // Теперь у нас есть количество прочитанных лекций, можно обновить TextView
                    lecred.setText(" прочитанных лекций: " + String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обработка ошибки, например, вывод Toast сообщения
                    Toast.makeText(MainActivity.this, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void countCompletedTasks() {
        DatabaseReference userLecturesRef = FirebaseDatabase.getInstance().getReference()
                .child("lectures").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userLecturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int completedLecturesCount = 0;
                for (DataSnapshot lectureSnapshot : dataSnapshot.getChildren()) {
                    if (lectureSnapshot.hasChild("complete") && lectureSnapshot.child("complete").getValue(Boolean.class)) {
                        completedLecturesCount++;
                    }
                }
                // Обновляем TextView с количеством выполненных лекций
                Taskcompled.setText(" Пройденных занятий: " + completedLecturesCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при загрузке данных
                Toast.makeText(MainActivity.this, "Ошибка при подсчете выполненных заданий", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNickname() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("users").child(userId).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nickname = dataSnapshot.getValue(String.class);
                        textViewNickname.setText(nickname);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обработка ошибки
                }
            });
        }
    }

    private void loadAvatar() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("users").child(userId).child("avatarUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String avatarUrl = dataSnapshot.getValue(String.class);
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            // Загрузка и отображение изображения
                            Glide.with(MainActivity.this).load(avatarUrl).into(imageViewAvatar);
                        } else {
                            // Если URL-адрес отсутствует или пустой, используйте плейсхолдер
                            imageViewAvatar.setImageResource(R.drawable.avatar_placeholder);
                        }
                    } else {
                        // Если URL-адрес отсутствует, используйте плейсхолдер
                        imageViewAvatar.setImageResource(R.drawable.avatar_placeholder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обработка ошибки
                }
            });
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите фото"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewAvatar.setImageBitmap(bitmap);
                uploadImageToFirebase(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = mStorage.child("avatars").child(mAuth.getCurrentUser().getUid() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("avatarUrl").setValue(imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        // Успешно сохранено в базе данных
                                        Toast.makeText(MainActivity.this, "Фото успешно сохранено", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Ошибка при сохранении в базе данных
                                        Toast.makeText(MainActivity.this, "Ошибка при сохранении фото", Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Ошибка при загрузке фото в хранилище
                        Toast.makeText(MainActivity.this, "Ошибка при загрузке фото", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, Authorization.class));
        finish(); // Закрываем текущую активность
    }
}

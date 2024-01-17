package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DetailedUserListActivity extends AppCompatActivity {

    private String selectedSection;
    private String selectedCourse;
    private String selectedBatch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_user_list);

        selectedSection = getIntent().getStringExtra("section");
        selectedCourse = getIntent().getStringExtra("course");// Retrieve the selected course
        selectedBatch = getIntent().getStringExtra("registrationYear");

        // Add logging to check the values of selectedBatch, selectedSection, and selectedCourse
        Log.d("Debug", "Selected Batch: " + selectedBatch);
        Log.d("Debug", "Selected Section: " + selectedSection);
        Log.d("Debug", "Selected Course: " + selectedCourse);


        setupUI();
    }

    private void setupUI() {
        LinearLayout userButtonLayout = findViewById(R.id.userButtonLayout);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("registrationYear", selectedBatch)
                .whereEqualTo("section", selectedSection)
                .whereEqualTo("course", selectedCourse)
                .whereEqualTo("role", "student")
                .whereEqualTo("status", "approved")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String username = document.getString("username");
                        String userId = document.getString("userId");

                        addUserButton(userButtonLayout, username, userId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void addUserButton(LinearLayout layout, final String username, final String userId) {
        Button userButton = new Button(this);
        userButton.setText(username);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle navigation to the detailed quiz results activity for the selected user
                // Pass the selected userId as an extra to the next activity
                navigateToQuizResults(userId);
            }
        });
        layout.addView(userButton);
    }

    private void navigateToQuizResults(String userId) {
        Intent intent = new Intent(this, DetailedQuizResultsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}


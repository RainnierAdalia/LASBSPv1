package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailedQuizResultsActivity extends AppCompatActivity {

    private String selectedUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_quiz_results);

        selectedUserId = getIntent().getStringExtra("userId");

        fetchAndDisplayQuizResults();
    }

    private void fetchAndDisplayQuizResults() {
        LinearLayout quizResultsLayout = findViewById(R.id.quizResultsLayout);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("quiz_results")
                .whereEqualTo("userId", selectedUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String quizId = document.getId();
                        int score = document.getLong("score").intValue();
                        int totalQuestions = document.getLong("totalQuestions").intValue();
                        String formattedDate = document.getString("formattedDate");
                        String quizTitle = document.getString("quizTitle");

                        // Inflate the quiz result item layout
                        View resultItemView = LayoutInflater.from(this).inflate(R.layout.quiz_result_layout, quizResultsLayout, false);

                        // Set the data to the TextViews in the inflated layout
                        TextView quizIdTextView = resultItemView.findViewById(R.id.quizIdTextView);
                        TextView quizTitleTextView = resultItemView.findViewById(R.id.quizTitleTextView);
                        TextView scoreTextView = resultItemView.findViewById(R.id.scoreTextView);
                        TextView totalQuestionsTextView = resultItemView.findViewById(R.id.totalQuestionsTextView);
                        TextView formattedDateTextView = resultItemView.findViewById(R.id.dateTextView);

                        quizIdTextView.setText(" " + quizId);
                        quizTitleTextView.setText(" " + quizTitle); // Set quizTitle
                        scoreTextView.setText(" " + score + "/" + totalQuestions);
                        totalQuestionsTextView.setText(" " + totalQuestions);
                        formattedDateTextView.setText(" " + formattedDate);

                        // Add the inflated layout to the parent LinearLayout
                        quizResultsLayout.addView(resultItemView);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching quiz results: " + e.getMessage());
                });
    }

}

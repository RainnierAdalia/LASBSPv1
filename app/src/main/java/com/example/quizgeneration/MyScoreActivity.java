package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyScoreActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_score);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchAndDisplayMyQuizResults();
    }

    private void fetchAndDisplayMyQuizResults() {
        LinearLayout quizResultsLayout = findViewById(R.id.quizResultsLayout);

        String currentUserUid = mAuth.getCurrentUser().getUid(); // Get the current user's UID

        db.collection("quiz_results")
                .whereEqualTo("userId", currentUserUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String quizId = document.getId();
                        int score = document.getLong("score").intValue();
                        int totalQuestions = document.getLong("totalQuestions").intValue();
                        String formattedDate = document.getString("formattedDate"); // Retrieve as String

                        // Fetch the quizTitle from the quiz_results collection
                        String quizTitle = document.getString("quizTitle");

                        // Inflate the layout for each quiz result
                        LinearLayout resultLayout = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.quiz_result_layout, null);

                        // Set values in the inflated layout
                        TextView quizIdTextView = resultLayout.findViewById(R.id.quizIdTextView);
                        TextView quizTitleTextView = resultLayout.findViewById(R.id.quizTitleTextView);
                        TextView scoreTextView = resultLayout.findViewById(R.id.scoreTextView);
                        TextView totalQuestionsTextView = resultLayout.findViewById(R.id.totalQuestionsTextView);
                        TextView dateTextView = resultLayout.findViewById(R.id.dateTextView);

                        quizIdTextView.setText(quizId);
                        quizTitleTextView.setText(quizTitle); // Set quizTitle
                        scoreTextView.setText(" " + score + "/" + totalQuestions);
                        totalQuestionsTextView.setText(String.valueOf(totalQuestions));
                        dateTextView.setText(formattedDate);

                        quizResultsLayout.addView(resultLayout);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching quiz results: " + e.getMessage());
                });
    }

}

package com.example.quizgeneration;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class QuizResult {
    private String quizTitle;
    private String quizId;
    private String userId;
    private int score;
    private int totalQuestions;
    private long timestamp;
    private String formattedDate; // New field

    public QuizResult() {
        // Required empty constructor for Firestore
    }

    public QuizResult(String quizTitle, String quizId, String userId, int score, int totalQuestions, long timestamp) {
        this.quizTitle = quizTitle;
        this.quizId = quizId;
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timestamp = timestamp;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public void saveToFirestore(Context context) {
        FirebaseFirestore.getInstance()
                .collection("quiz_results")
                .add(this)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Quiz result saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error saving quiz result: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}

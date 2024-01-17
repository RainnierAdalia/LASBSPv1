package com.example.quizgeneration;

public class QuizSubmission {
    private String userId;
    private String quizId;

    // Empty constructor required for Firestore
    public QuizSubmission() {
    }

    public QuizSubmission(String userId, String quizId) {
        this.userId = userId;
        this.quizId = quizId;
    }

    public String getUserId() {
        return userId;
    }

    public String getQuizId() {
        return quizId;
    }
}

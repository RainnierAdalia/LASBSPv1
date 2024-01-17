package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuizTitleActivity extends AppCompatActivity {

    private EditText quizTitleEditText;
    private Button createQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_title);

        quizTitleEditText = findViewById(R.id.quizTitleEditText);
        createQuizButton = findViewById(R.id.createQuizButton);

        createQuizButton.setOnClickListener(v -> startQuiz());
    }

    private void startQuiz() {
        String quizTitle = quizTitleEditText.getText().toString();

        if (TextUtils.isEmpty(quizTitle)) {
            Toast.makeText(this, "Please enter a Quiz Title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pass the quizTitle to the new QuizActivity
        Intent intent = new Intent(QuizTitleActivity.this, QuizActivity.class);
        intent.putExtra("QUIZ_TITLE", quizTitle);
        startActivity(intent);
    }
}

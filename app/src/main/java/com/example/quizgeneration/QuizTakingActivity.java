package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QuizTakingActivity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private RadioButton optionARadioButton, optionBRadioButton, optionCRadioButton, optionDRadioButton;
    private TextView timerTextView;
    private Button submitButton;
    private int loadedQuestionsCount = 0;

    private Quiz currentQuiz;
    private List<QuizQuestion> allQuestions; // List to store all questions
    private int currentQuestionIndex;
    private CountDownTimer timer;
    private View loadingView;

    private FirebaseFirestore db;

    // Add a field to store the selected option for the current question
    private String selectedOptionForCurrentQuestion;

    // Add a field to store the selected options for all questions
    private List<String> selectedOptions;

    private boolean isQuizSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_taking);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        optionARadioButton = findViewById(R.id.optionARadioButton);
        optionBRadioButton = findViewById(R.id.optionBRadioButton);
        optionCRadioButton = findViewById(R.id.optionCRadioButton);
        optionDRadioButton = findViewById(R.id.optionDRadioButton);
        timerTextView = findViewById(R.id.timerTextView);
        submitButton = findViewById(R.id.submitButton);
        loadingView = findViewById(R.id.loadingView); // Initialize loading view reference

        // Load the quiz based on the ID passed from the previous activity
        String quizId = getIntent().getStringExtra("quizId");
        loadQuizFromFirestore(quizId);

        // Initialize the list to store selected options
        selectedOptions = new ArrayList<>();
    }

    // Add this method to show/hide the loading view
    private void showLoading(boolean show) {
        if (show) {
            loadingView.setVisibility(View.VISIBLE);
        } else {
            loadingView.setVisibility(View.GONE);
        }
    }

    private void loadQuizFromFirestore(String quizId) {
        db.collection("quizzes").document(quizId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            currentQuiz = document.toObject(Quiz.class);
                            if (currentQuiz != null && currentQuiz.getQuestionIds() != null && !currentQuiz.getQuestionIds().isEmpty()) {
                                loadAllQuestions(currentQuiz.getQuestionIds());
                            } else {
                                // Handle the case where the quiz has no questions
                                showErrorMessage("This quiz has no questions.");
                            }
                        } else {
                            // Handle the case where the quiz document does not exist
                            showErrorMessage("Quiz not found.");
                        }
                    } else {
                        // Handle the exception
                        showErrorMessage("Error loading quiz. Please try again later.");
                    }
                });
    }

    private void loadAllQuestions(List<String> questionIds) {
        allQuestions = new ArrayList<>();
        loadedQuestionsCount = 0;

        // Initialize selectedOptions list
        selectedOptions = new ArrayList<>(Collections.nCopies(questionIds.size(), ""));

        int totalQuestions = questionIds.size();

        for (String questionId : questionIds) {
            db.collection("questions").document(questionId)
                    .get()
                    .addOnCompleteListener((Task<DocumentSnapshot> task) -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                QuizQuestion question = document.toObject(QuizQuestion.class);
                                allQuestions.add(question);
                                loadedQuestionsCount++;

                                // Check if all questions are loaded
                                if (loadedQuestionsCount == totalQuestions) {
                                    // All questions are loaded, display the first question and start the timer
                                    currentQuestionIndex = 0;
                                    loadAndDisplayQuestion(currentQuestionIndex);
                                    showLoading(false); // Hide the loading view
                                }
                            } else {
                                // Handle the case where the question document does not exist
                                showErrorMessage("Question not found.");
                            }
                        } else {
                            // Handle the exception
                            showErrorMessage("Error loading question. Please try again later.");
                        }
                    });
        }

        showLoading(true); // Show the loading view
    }

    private void loadAndDisplayQuestion(int index) {
        // Clear the selected radio button for each new question
        optionsRadioGroup.clearCheck();

        if (index < allQuestions.size()) {
            QuizQuestion currentQuestion = allQuestions.get(index);

            // Update UI elements with the current question details
            questionTextView.setText(currentQuestion.getQuestion());
            updateRadioButtonTexts(currentQuestion.getOptions());

            // Enable the submit button when a new question is loaded
            submitButton.setEnabled(true);

            // Set up timer for the current question using quiz-level duration
            startQuestionTimer(currentQuiz, index, currentQuiz.getQuestionIds());

            // Initialize selected option for the current question
            selectedOptionForCurrentQuestion = "";

            // Retrieve the selected option from the list if it exists
            if (index < selectedOptions.size()) {
                selectedOptionForCurrentQuestion = selectedOptions.get(index);
                setRadioButtonChecked(selectedOptionForCurrentQuestion);
            }
        } else {
            // All questions have been answered, submit quiz or show completion message
            finishQuiz();
        }
    }

    private void updateRadioButtonTexts(Map<String, String> options) {
        optionARadioButton.setText("A. " + options.get("a"));
        optionBRadioButton.setText("B. " + options.get("b"));
        optionCRadioButton.setText("C. " + options.get("c"));
        optionDRadioButton.setText("D. " + options.get("d"));
    }

    private void startQuestionTimer(Quiz currentQuiz, int index, List<String> questionIds) {
        if (timer != null) {
            timer.cancel();
        }

        long questionDurationMillis = currentQuiz.getTimerDurationMillis();
        if (questionDurationMillis > 0) {
            timer = new CountDownTimer(questionDurationMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTextView.setText("Time left: " + formatTime(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    moveToNextQuestion(index, questionIds);
                    timer.cancel();
                }
            }.start();
        } else {
            showErrorMessage("Invalid quiz duration");
        }
    }

    private void moveToNextQuestion(int index, List<String> questionIds) {
        currentQuestionIndex++;
        loadAndDisplayQuestion(currentQuestionIndex);
    }

    private void finishQuiz() {
        if (!isQuizSubmitted) {
            int correctAnswers = calculateCorrectAnswers();

            // Save the quiz result
            saveQuizResult(correctAnswers);

            // Save the quiz submission
            saveQuizSubmission();

            showScoreDialog(correctAnswers);

            // Set the flag to true after submitting the quiz
            isQuizSubmitted = true;
        }
    }

    private void saveQuizResult(int correctAnswers) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String quizTitle = currentQuiz.getTitle();
            String quizId = currentQuiz.getId();
            long timestamp = System.currentTimeMillis();

            QuizResult quizResult = new QuizResult(quizTitle, quizId, userId, correctAnswers, allQuestions.size(), timestamp);

            // Save the formatted date
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(timestamp));
            quizResult.setFormattedDate(formattedDate);

            // Save the quiz result to Firestore
            quizResult.saveToFirestore(this);
        } else {
            // Handle the case where the user is not signed in
        }
    }

    private void saveQuizSubmission() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String quizId = currentQuiz.getId();

            QuizSubmission quizSubmission = new QuizSubmission(userId, quizId);

            // Save the quiz submission to Firestore
            FirebaseFirestore.getInstance()
                    .collection("quiz_submissions")
                    .add(quizSubmission)
                    .addOnSuccessListener(documentReference -> {
                        // Submission saved successfully
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        } else {
            // Handle the case where the user is not signed in
        }
    }


    private int calculateCorrectAnswers() {
        int correctAnswers = 0;

        for (int i = 0; i < allQuestions.size(); i++) {
            String selectedOption = selectedOptions.get(i);
            QuizQuestion question = allQuestions.get(i);

            // Debugging prints
            System.out.println("Selected option: " + selectedOption);
            System.out.println("Correct option: " + question.getCorrectOptionLabel());
            System.out.println("Is correct? " + question.getCorrectOptionLabel().equalsIgnoreCase(selectedOption));

            if (selectedOption != null && !selectedOption.isEmpty() &&
                    question.getCorrectOptionLabel() != null &&
                    question.getCorrectOptionLabel().equalsIgnoreCase(selectedOption)) {
                correctAnswers++;
            }
        }

        return correctAnswers;
    }

    private String getSelectedOptionForQuestionForCalculation() {
        // Retrieve the selected option from the list if it exists
        if (currentQuestionIndex < selectedOptions.size()) {
            return selectedOptions.get(currentQuestionIndex);
        }

        return ""; // Return an empty string if no option is selected
    }

    public void onSubmitButtonClick(View view) {
        if (allQuestions != null && currentQuestionIndex < allQuestions.size()) {
            QuizQuestion question = allQuestions.get(currentQuestionIndex);

            // Debugging: Print the selected option
            selectedOptionForCurrentQuestion = getSelectedOptionFromRadioGroup();
            System.out.println("Selected option for current question: " + selectedOptionForCurrentQuestion);

            boolean isCorrect = question.getCorrectOptionLabel() != null &&
                    question.getCorrectOptionLabel().equalsIgnoreCase(selectedOptionForCurrentQuestion);

            if (isCorrect) {
                // Correct answer, handle as needed
            } else {
                // Incorrect answer, handle as needed
            }

            // Save the selected option for the current question with its ID
            selectedOptions.set(currentQuestionIndex, selectedOptionForCurrentQuestion);

            // Clear the selected radio button
            optionsRadioGroup.clearCheck();

            // Move to the next question
            moveToNextQuestion(currentQuestionIndex, currentQuiz.getQuestionIds());
        } else {
            showErrorMessage("Error loading question. Please try again later.");
        }
    }

    private String getSelectedOptionFromRadioGroup() {
        for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
            View radioButton = optionsRadioGroup.getChildAt(i);

            if (radioButton instanceof RadioButton) {
                RadioButton currentRadioButton = (RadioButton) radioButton;

                if (currentRadioButton.isChecked()) {
                    // Extract the option label (a, b, c, d) from the text
                    return currentRadioButton.getText().toString().substring(0, 1).toLowerCase();
                }
            }
        }

        return ""; // Return an empty string if no option is selected
    }

    private void showScoreDialog(int userScore) {
        if (!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Quiz Completed");
            builder.setMessage("Your Score: " + userScore + "/" + allQuestions.size());
            builder.setPositiveButton("OK", (dialog, which) -> navigateToMainActivity());

            try {
                builder.show();
            } catch (WindowManager.BadTokenException e) {
                // Handle the exception, or simply log it
                e.printStackTrace();
            }
        }
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String formatTime(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setRadioButtonChecked(String selectedOption) {
        for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
            View radioButton = optionsRadioGroup.getChildAt(i);

            if (radioButton instanceof RadioButton) {
                RadioButton currentRadioButton = (RadioButton) radioButton;

                if (currentRadioButton.getText().toString().substring(3).trim().equalsIgnoreCase(selectedOption)) {
                    currentRadioButton.setChecked(true);
                    break;
                }
            }
        }
    }
}

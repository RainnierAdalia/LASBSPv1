package com.example.quizgeneration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;



import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuizActivity extends AppCompatActivity {

    private EditText questionEditText;
    private EditText option1EditText;
    private EditText option2EditText;
    private EditText option3EditText;
    private EditText option4EditText;
    private EditText correctOptionIndexEditText;
    private Button addQuestionButton;
    private Button saveButton;
    private Button doneButton;
    private ImageView qrCodeImageView;
    private String quizTitle;
    private Spinner timerSpinner; // Use Spinner for timer options
    private CountDownTimer questionTimer;
    private long timerDurationMillis;

    private FirebaseFirestore db;

    private List<QuizQuestion> addedQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        db = FirebaseFirestore.getInstance();

        questionEditText = findViewById(R.id.questionEditText);
        option1EditText = findViewById(R.id.option1EditText);
        option2EditText = findViewById(R.id.option2EditText);
        option3EditText = findViewById(R.id.option3EditText);
        option4EditText = findViewById(R.id.option4EditText);
        correctOptionIndexEditText = findViewById(R.id.correctOptionIndexEditText);
        addQuestionButton = findViewById(R.id.addQuestionButton);
        saveButton = findViewById(R.id.saveButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        doneButton = findViewById(R.id.doneButton);

        timerSpinner = findViewById(R.id.timerSpinner); // Initialize timerSpinner

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.timer_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timerSpinner.setAdapter(adapter);

        timerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = parentView.getItemAtPosition(position).toString();
                updateTimerDuration(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        addQuestionButton.setOnClickListener(v -> {
            checkUserApprovalStatus();
            startTimer(); // Start timer when adding a new question
        });

        saveButton.setOnClickListener(v -> saveQuiz());
        doneButton.setOnClickListener(v -> navigateToMainActivity());

        Intent intent = getIntent();
        if (intent != null) {
            quizTitle = intent.getStringExtra("QUIZ_TITLE");
        }
    }

    private void updateTimerDuration(String selectedOption) {
        switch (selectedOption) {
            case "30 seconds":
                timerDurationMillis = 30 * 1000;
                break;
            case "45 seconds":
                timerDurationMillis = 45 * 1000;
                break;
            case "1 minute":
                timerDurationMillis = 60 * 1000;
                break;
            case "No Timer":
                timerDurationMillis = 0;
                break;
            default:
                timerDurationMillis = 30 * 1000;
        }
    }

    private void startTimer() {
        if (questionTimer != null) {
            questionTimer.cancel(); // Cancel previous timer if it exists
        }

        questionTimer = new CountDownTimer(timerDurationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update UI or perform actions on each tick if needed
            }

            @Override
            public void onFinish() {
                // Timer finished, perform actions (e.g., submit quiz) if needed
            }
        }.start();
    }

    private Bitmap generateQRCode(String quizId, List<String> questionIds) {
        try {
            String dataToEncode = quizId + "|" + TextUtils.join(",", questionIds);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    dataToEncode,
                    BarcodeFormat.QR_CODE,
                    500, 500, null
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addQuestion() {
        if (addedQuestions.size() >= 10) {
            Toast.makeText(this, "You can only add up to 10 questions to the quiz.", Toast.LENGTH_SHORT).show();
            return;
        }

        String question = questionEditText.getText().toString();
        String option1 = option1EditText.getText().toString();
        String option2 = option2EditText.getText().toString();
        String option3 = option3EditText.getText().toString();
        String option4 = option4EditText.getText().toString();
        String correctOptionLabel = correctOptionIndexEditText.getText().toString().toLowerCase();

        if (!isValidCorrectOptionLabel(correctOptionLabel)) {
            Toast.makeText(this, "Invalid correct option label. Please use 'a', 'b', 'c', or 'd'.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> options = new HashMap<>();
        options.put("a", option1);
        options.put("b", option2);
        options.put("c", option3);
        options.put("d", option4);

        QuizQuestion quizQuestion = new QuizQuestion(question, options, correctOptionLabel);

        final String[] questionId = {generateQuestionId()}; // Declare as final array

        db.collection("questions")
                .add(quizQuestion)
                .addOnSuccessListener(documentReference -> {
                    questionId[0] = documentReference.getId();
                    Toast.makeText(this, "Question added with ID: " + questionId[0], Toast.LENGTH_SHORT).show();

                    quizQuestion.setId(questionId[0]);

                    addedQuestions.add(quizQuestion);

                    clearFormInputs();
                })
                .addOnFailureListener(e -> {
                    Log.e("QuizActivity", "Failed to add question", e);
                    Toast.makeText(this, "Failed to add question", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isValidCorrectOptionLabel(String label) {
        String lowerCaseLabel = label.toLowerCase();
        return lowerCaseLabel.equals("a") || lowerCaseLabel.equals("b") || lowerCaseLabel.equals("c") || lowerCaseLabel.equals("d");
    }

    private String generateQuestionId() {
        return UUID.randomUUID().toString();
    }

    private void clearFormInputs() {
        questionEditText.getText().clear();
        option1EditText.getText().clear();
        option2EditText.getText().clear();
        option3EditText.getText().clear();
        option4EditText.getText().clear();
        correctOptionIndexEditText.getText().clear();
    }

    private void saveQuiz() {
        if (addedQuestions.isEmpty()) {
            Toast.makeText(this, "Please add questions to the quiz before saving.", Toast.LENGTH_SHORT).show();
            return;
        }

        Quiz quiz = new Quiz(quizTitle, getQuestionIds(addedQuestions));

        quiz.setTimerDurationMillis(timerDurationMillis);

        db.collection("quizzes")
                .add(quiz)
                .addOnSuccessListener(documentReference -> {
                    String generatedQuizId = documentReference.getId();

                    String questionIdsString = generateQuestionIdsString(quiz.getQuestionIds());
                    Bitmap qrCodeBitmap = generateQRCode(generatedQuizId, quiz.getQuestionIds());

                    generateAndDisplayQRCode(generatedQuizId, qrCodeBitmap);

                    uploadQRCodeImageAndSaveQuiz(generatedQuizId, qrCodeBitmap, quiz.getQuestionIds(), quiz.getTimerDurationMillis());

                    addedQuestions.clear();
                    currentQuestionIndex = 0;

                    clearFormInputs();
                })
                .addOnFailureListener(e -> {
                    handleSaveQuizFailure(e);
                });
    }

    private void handleSaveQuizFailure(Exception e) {
        Log.e("QuizActivity", "Failed to save quiz", e);
        Toast.makeText(QuizActivity.this, "Failed to save quiz", Toast.LENGTH_SHORT).show();
    }

    private void generateAndDisplayQRCode(String quizId, Bitmap qrCodeBitmap) {
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateQuestionIdsString(List<String> questionIds) {
        StringBuilder builder = new StringBuilder();

        for (String questionId : questionIds) {
            builder.append(questionId).append(",");
        }

        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private List<String> getQuestionIds(List<QuizQuestion> questions) {
        List<String> questionIds = new ArrayList<>();
        for (QuizQuestion question : questions) {
            questionIds.add(question.getId());
        }
        return questionIds;
    }

    private void uploadQRCodeImageAndSaveQuiz(String quizId, Bitmap qrCodeBitmap, List<String> questionIds, long timerDurationMillis) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference qrCodeImageRef = storageRef.child("qrcodes/" + quizId + ".png");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();

        UploadTask uploadTask = qrCodeImageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
                    qrCodeImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String qrCodeImageUrl = uri.toString();

                        Quiz quiz = new Quiz(quizTitle, questionIds);
                        quiz.setId(quizId);
                        quiz.setQrCodeImageUrl(qrCodeImageUrl);
                        quiz.setTimerDurationMillis(timerDurationMillis);

                        db.collection("quizzes").document(quizId)
                                .set(quiz)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(QuizActivity.this, "Quiz saved with ID: " + quizId, Toast.LENGTH_SHORT).show();
                                    addedQuestions.clear();
                                    currentQuestionIndex = 0;
                                    clearFormInputs();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("QuizActivity", "Failed to save quiz", e);
                                    Toast.makeText(QuizActivity.this, "Failed to save quiz", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("QuizActivity", "Failed to upload QR code image", e);
                    Toast.makeText(QuizActivity.this, "Failed to upload QR code image", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(QuizActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional: finish() to close the current activity if you don't want to come back to it
    }

    private void checkUserApprovalStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You need to sign in to add a question.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        String status = documentSnapshot.getString("status");

                        if ("instructor".equals(role) && "approved".equals(status)) {
                            addQuestion();
                        } else {
                            Toast.makeText(this, "You need to be an approved instructor to add a question.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error fetching user data. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QuizActivity", "Failed to fetch user data", e);
                    Toast.makeText(this, "Failed to fetch user data. Please try again later.", Toast.LENGTH_SHORT).show();
                });
    }
}

package com.example.quizgeneration;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizListActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        listView = findViewById(R.id.listView);

        // List all quizzes
        listAllQuizzes();

        // Set up item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected quiz
            Quiz selectedQuiz = (Quiz) parent.getItemAtPosition(position);

            // Start QRCodeScannerActivity with the selected quizId
            startQRCodeScannerActivity(selectedQuiz.getId());
        });
    }

    private void listAllQuizzes() {
        FirebaseFirestore.getInstance()
                .collection("quizzes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Quiz> quizzes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Quiz quiz = document.toObject(Quiz.class);
                        quizzes.add(quiz);
                    }

                    // Display the list of quizzes
                    displayQuizList(quizzes);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching quizzes.", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayQuizList(List<Quiz> quizzes) {
        // Create a custom ArrayAdapter to display the list of quizzes in a ListView
        ArrayAdapter<Quiz> adapter = new ArrayAdapter<Quiz>(this, R.layout.quiz_list_item, quizzes) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.quiz_list_item, parent, false);
                }

                // Get the quiz at the current position
                Quiz quiz = getItem(position);

                // Set the quiz title and ID in the TextViews
                TextView titleTextView = convertView.findViewById(R.id.quizTitleTextView);
                TextView idTextView = convertView.findViewById(R.id.quizIdTextView);

                titleTextView.setText(quiz.getTitle());
                idTextView.setText("ID: " + quiz.getId());

                return convertView;
            }
        };

        // Set the adapter for the ListView
        listView.setAdapter(adapter);
    }

    public void onScanButtonClick(View view) {
        Log.d("QuizListActivity", "onScanButtonClick called");
        // Get the selected quizId
        View parentView = (View) view.getParent();
        TextView idTextView = parentView.findViewById(R.id.quizIdTextView);
        String quizId = idTextView.getText().toString().replace("ID: ", "");

        // Start QRCodeScannerActivity with the selected quizId
        startQRCodeScannerActivity(quizId);
    }

    public void onUploadButtonClick(View view) {
        Log.d("QuizListActivity", "onUploadButtonClick called");
        // Get the selected quizId
        View parentView = (View) view.getParent();
        TextView idTextView = parentView.findViewById(R.id.quizIdTextView);
        String quizId = idTextView.getText().toString().replace("ID: ", "");

        // Check whether to start QRCodeScannerActivity or QRCodeImageUploaderActivity
        if (shouldStartImageUploader()) {
            startImageUploaderActivity(quizId);
        } else {
            startQRCodeScannerActivity(quizId);
        }
    }

    private void startQRCodeScannerActivity(String quizId) {
        Intent intent = new Intent(QuizListActivity.this, QRCodeScannerActivity.class);
        intent.putExtra("QUIZ_ID", quizId);
        startActivityForResult(intent, QR_CODE_SCANNER_REQUEST_CODE);
    }

    private void startImageUploaderActivity(String quizId) {
        Intent intent = new Intent(QuizListActivity.this, QRCodeImageUploaderActivity.class);
        intent.putExtra("QUIZ_ID", quizId);
        startActivity(intent);
    }

    private boolean shouldStartImageUploader() {
        // Replace this logic with your actual condition
        return true;  // For demonstration, always start the image uploader
    }

    // Add this constant at the class level
    private static final int QR_CODE_SCANNER_REQUEST_CODE = 123;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QR_CODE_SCANNER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Handle the result from QRCodeScannerActivity if needed
                if (data != null) {
                    // For example, you can get scanned data
                    String scannedData = data.getStringExtra("SCANNED_DATA");
                    Toast.makeText(this, "Scanned Data: " + scannedData, Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle if the QR code scanning was canceled
                Toast.makeText(this, "QR Code scanning canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

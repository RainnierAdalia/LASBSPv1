package com.example.quizgeneration;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class QRCodeImageUploaderActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int QR_CODE_SCANNER_REQUEST_CODE = 123;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_image_uploader);

        imageView = findViewById(R.id.imageView);

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, start QR code scanner
            startQRCodeScanner();
        }
    }

    private void startQRCodeScanner() {
        // Launch ZXing QR code scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    // Retrieve scanned data
                    String scannedData = result.getContents();

                    // Decode the scanned data
                    Quiz scannedQuiz = decodeScannedData(scannedData);

                    // Check if the scanned quiz matches the expected quiz
                    if (scannedQuiz != null && isExpectedQuiz(scannedQuiz)) {
                        // Navigate to QuizTakingActivity
                        navigateToQuizTakingActivity(scannedQuiz);
                    } else {
                        Toast.makeText(this, "Scanned QR code does not match the expected quiz.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    // Handle if the QR code scanning was canceled
                    Toast.makeText(this, "QR Code scanning canceled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private Quiz decodeScannedData(String scannedData) {
        try {
            // Parse the scanned data into a Quiz object
            // Modify this method based on your QR code content
            // For example, if your QR code content is in JSON format, you might use a JSON parser
            // Here, I'm assuming a simple format where the quiz ID and question IDs are separated by "|"
            String[] parts = scannedData.split("\\|");
            if (parts.length == 2) {
                String quizId = parts[0];
                List<String> questionIds = Arrays.asList(parts[1].split(","));

                // You might need to fetch additional information from Firestore based on quizId
                Quiz quiz = new Quiz();
                quiz.setId(quizId);
                quiz.setQuestionIds(questionIds);

                return quiz;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error decoding QR code", Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }
    }

    private boolean isExpectedQuiz(Quiz scannedQuiz) {
        // Compare the scanned quiz with the expected quiz
        // For example, you might compare quiz IDs or other relevant information
        String expectedQuizId = getIntent().getStringExtra("EXPECTED_QUIZ_ID");
        return expectedQuizId != null && expectedQuizId.equals(scannedQuiz.getId());
    }

    private void navigateToQuizTakingActivity(Quiz quiz) {
        // Navigate to QuizTakingActivity
        Intent intent = new Intent(QRCodeImageUploaderActivity.this, QuizTakingActivity.class);
        intent.putExtra("quizId", quiz.getId());
        intent.putStringArrayListExtra("questionIds", new ArrayList<>(quiz.getQuestionIds()));
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start QR code scanner
                startQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}

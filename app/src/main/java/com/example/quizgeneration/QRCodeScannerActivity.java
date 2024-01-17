package com.example.quizgeneration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QRCodeScannerActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private CaptureManager captureManager;
    private DecoratedBarcodeView barcodeView;
    private boolean isFirstScan = true;
    private List<String> questionIds;  // Declare questionIds at the class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startScanner();
        }
    }

    private void startScanner() {
        captureManager = new CaptureManager(this, barcodeView);
        captureManager.decode();

        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (isFirstScan) {
                    isFirstScan = false;
                    handleScannedData(result.getText());
                }
            }
        });
    }

    private void stopScanner() {
        captureManager.onPause();
        finish();
    }

    // Modify the createSubmissionDocumentId method
    private String createSubmissionDocumentId(String userId, String scannedQuizId) {
        return userId + scannedQuizId;  // Concatenate without a separator
    }

    // Modify the handleScannedData method
    private void handleScannedData(String scannedData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String[] qrCodeDataParts = scannedData.split("\\|");

        if (qrCodeDataParts.length == 2) {
            String scannedQuizId = qrCodeDataParts[0];
            String questionIdsString = qrCodeDataParts[1];

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "You need to sign in to participate in the quiz.", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = currentUser.getUid();
            Log.d("QRCodeScanner", "User ID: " + userId);

            String submissionDocumentId = createSubmissionDocumentId(userId, scannedQuizId);
            Log.d("QRCodeScanner", "Submission Document ID: " + submissionDocumentId);

            // Check if the scanned quiz ID matches the selected quiz ID
            if (scannedQuizId.equals(getIntent().getStringExtra("QUIZ_ID"))) {
                // Fetch and navigate to QuizTakingActivity
                this.questionIds = Arrays.asList(questionIdsString.split(","));
                fetchQuestionsAndNavigateToQuizTakingActivity(userId, scannedQuizId);
            } else {
                Toast.makeText(this, "Scanned quiz does not match the selected quiz.", Toast.LENGTH_SHORT).show();
                stopScanner();
            }
        } else {
            Toast.makeText(this, "Invalid QR code data format. Expected 2 parts, but got " + qrCodeDataParts.length, Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchQuestionsAndNavigateToQuizTakingActivity(String userId, String quizId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("QRCodeScanner", "Checking quiz submission for userId: " + userId + ", quizId: " + quizId);

        // Check if the quiz_submissions document exists for the user and quiz
        db.collection("quiz_submissions")
                .whereEqualTo("userId", userId)
                .whereEqualTo("quizId", quizId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot submissions = task.getResult();
                        if (submissions != null && !submissions.isEmpty()) {
                            // Submission document exists, prevent access
                            Toast.makeText(this, "You have already taken this quiz.", Toast.LENGTH_SHORT).show();
                            Log.d("QRCodeScanner", "User has already taken this quiz.");

                            // Add additional logic to prevent access if needed
                            // For example, you can finish the activity to prevent further interaction
                            stopScanner();
                        } else {
                            // Submission document doesn't exist, allow access
                            Toast.makeText(this, "You haven't taken this quiz yet. Proceeding...", Toast.LENGTH_SHORT).show();
                            Log.d("QRCodeScanner", "User has not taken this quiz. Proceeding...");
                            // Now you can navigate to QuizTakingActivity
                            navigateToQuizTakingActivity(quizId);
                        }
                    } else {
                        // Handle the case when checking quiz submission fails
                        Toast.makeText(this, "Failed to check quiz submission: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("QRCodeScanner", "Failed to check quiz submission", task.getException());

                        // If there is an error, let's proceed as if the user has not taken the quiz
                        Log.d("QRCodeScanner", "Error checking quiz submission. Proceeding as if not taken.");
                        navigateToQuizTakingActivity(quizId);
                    }
                });
    }


    private void navigateToQuizTakingActivity(String quizId) {
        Intent intent = new Intent(QRCodeScannerActivity.this, QuizTakingActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putStringArrayListExtra("questionIds", new ArrayList<>(questionIds));
        startActivity(intent);

        stopScanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}

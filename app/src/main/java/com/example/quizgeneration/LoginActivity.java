package com.example.quizgeneration;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform login
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    performLogin(email, password);
                }
            }
        });

        // Find the TextView with ID RegisterNow
        TextView registerNowTextView = findViewById(R.id.RegisterNow);
        registerNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RegisterActivity when the TextView is clicked
                openRegisterActivity();
            }
        });
    }

    private void performLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                checkUserStatus(userId);
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserApprovalStatus(String userId) {
        // Check approval status from 'users' collection
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(approvalTask -> {
                    if (approvalTask.isSuccessful() && approvalTask.getResult() != null) {
                        DocumentSnapshot approvalSnapshot = approvalTask.getResult();
                        String userStatus = approvalSnapshot.getString("status");

                        if (userStatus != null && userStatus.equals("approved")) {
                            // User is approved, allow access to MainActivity
                            Log.d("FirestoreDebug", "User is approved. UserStatus: " + userStatus);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (userStatus != null && userStatus.equals("pending")) {
                            // User is pending approval, restrict access to MainActivity
                            Log.d("FirestoreDebug", "User is pending approval. UserStatus: " + userStatus);
                            Toast.makeText(LoginActivity.this, "Access Denied: User pending approval", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            // Handle other status cases if needed
                            Log.d("FirestoreDebug", "Access Denied. UserStatus: " + userStatus);
                            Toast.makeText(LoginActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    } else {
                        // Debug log to check if approvalTask.isSuccessful() condition is met
                        Log.d("FirestoreDebug", "Failed to retrieve user approval data. Task success: " + approvalTask.isSuccessful());
                        Toast.makeText(LoginActivity.this, "Failed to retrieve user approval data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserStatus(String userId) {
        // Check email verification status
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            // User email is verified, check approval status
            checkUserApprovalStatus(userId);
        } else {
            // Email is not verified, show a toast and sign out
            Toast.makeText(LoginActivity.this, "Email not verified. Check your email for verification link.", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
        }
    }


    private void openRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}

    package com.example.quizgeneration;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;

    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;

    public class MainActivity extends AppCompatActivity {

        private Button quizButton;
        private Button logoutButton;
        private Button checkUserButton;
        private Button myScoreButton;
        private Button uploadModuleButton;
        private Button viewModuleButton;
        private Button checkRegistrationButton;
        private Button scanQuizButton;
        private static final int REQUEST_CODE_REGISTER = 101;
        private static final int REQUEST_CODE_SCAN_QUIZ = 201;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            quizButton = findViewById(R.id.quizButton);
            logoutButton = findViewById(R.id.logoutButton);
            scanQuizButton = findViewById(R.id.scanQuizButton);
            checkUserButton = findViewById(R.id.checkUserButton);
            myScoreButton = findViewById(R.id.myScoreButton);
            uploadModuleButton = findViewById(R.id.uploadModuleButton);
            viewModuleButton = findViewById(R.id.viewModuleButton);
            checkRegistrationButton = findViewById(R.id.checkRegistrationButton);


            quizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, QuizTitleActivity.class);
                    startActivity(intent);
                }
            });

            checkRegistrationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ModifyUserStatusActivity.class);
                    startActivity(intent);
                }
            });

            checkUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, BatchSelectionActivity.class);
                    startActivity(intent);
                }
            });

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            scanQuizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the QR code scanning activity to scan the quiz QR code
                    Intent intent = new Intent(MainActivity.this, QuizListActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN_QUIZ);
                }
            });

            myScoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(MainActivity.this, MyScoreActivity.class);
                    startActivity(intent);
                }
            });
            uploadModuleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(intent);
                }
            });
            viewModuleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(MainActivity.this, ModuleMapActivity.class);
                    startActivity(intent);
                }
            });

            checkUserAccess();
        }
        private void checkUserAccess() {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .document(currentUser.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    String userRole = documentSnapshot.getString("role");
                                    String userStatus = documentSnapshot.getString("status");
                                    if (userStatus != null && userStatus.equals("approved")) {
                                        if (userRole != null && userRole.equals("instructor")) {
                                            // User is an approved instructor, allow access to MainActivity
                                            // Perform necessary operations for instructor access
                                            displayUserInfo(documentSnapshot);
                                            checkUserButton.setVisibility(View.VISIBLE);
                                            myScoreButton.setVisibility(View.GONE);
                                            uploadModuleButton.setVisibility(View.VISIBLE);
                                            checkRegistrationButton.setVisibility(View.VISIBLE);
                                            scanQuizButton.setVisibility(View.GONE);

                                        } else if (userRole != null && userRole.equals("student")) {
                                            // User is a student, restrict access to some buttons
                                            quizButton.setVisibility(View.GONE);
                                            uploadModuleButton.setVisibility(View.GONE);
                                            checkUserButton.setVisibility(View.GONE);
                                            myScoreButton.setVisibility(View.VISIBLE);
                                            checkRegistrationButton.setVisibility(View.GONE);
                                        }
                                    } else if (userStatus != null && userStatus.equals("pending")) {
                                        // User is a pending user, redirect to registration
                                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                        startActivityForResult(intent, REQUEST_CODE_REGISTER);
                                    } else {
                                        // Handle other status cases if needed
                                        Toast.makeText(MainActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Failed to retrieve user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        });
            } else {
                // If the user is not logged in, navigate to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER);
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
                // Registration successful, check user access again
                checkUserAccess();
            }
        }

        private void displayUserInfo(DocumentSnapshot documentSnapshot) {
            String username = documentSnapshot.getString("username");
            String course = documentSnapshot.getString("course");
            String section = documentSnapshot.getString("section");
            String role = documentSnapshot.getString("role");

            TextView usernameTextView = findViewById(R.id.usernameTextView);
            TextView courseTextView = findViewById(R.id.courseTextView);
            TextView sectionTextView = findViewById(R.id.sectionTextView);
            TextView roleTextView = findViewById(R.id.roleTextView);

            usernameTextView.setText("Username: " + username);
            courseTextView.setText("Course: " + course);
            sectionTextView.setText("Section: " + section);
            roleTextView.setText("Role: " + role);
        }

    }
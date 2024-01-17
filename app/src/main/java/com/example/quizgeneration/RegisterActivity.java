package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements User.UserRegistrationCallback {
    private EditText lastnameEditText, firstnameEditText, middlenameEditText, idnumEditText, emailEditText, passwordEditText;
    private Spinner courseSpinner, sectionSpinner, registrationYear;
    private ProgressBar progressBar;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        lastnameEditText = findViewById(R.id.Lastname);
        firstnameEditText = findViewById(R.id.first);
        middlenameEditText = findViewById(R.id.middle);
        idnumEditText = findViewById(R.id.ID_Number);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        courseSpinner = findViewById(R.id.courseSpinner);
        sectionSpinner = findViewById(R.id.sectionSpinner);
        progressBar = findViewById(R.id.progress_bar);
        registerButton = findViewById(R.id.registerButton);
        registrationYear = findViewById(R.id.registrationYearSpinner);  // Initialize registrationYearSpinner


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        TextView gotoLogin = findViewById(R.id.loginNow);
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RegisterActivity when the TextView is clicked
                goToLoginActivity();
            }
        });
    }


    private void registerUser() {
        String lastname = lastnameEditText.getText().toString().trim();
        String firstname = firstnameEditText.getText().toString().trim();
        String middlename = middlenameEditText.getText().toString().trim();
        Integer idnum;
        try {
            idnum = Integer.parseInt(idnumEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ID Number", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String course = courseSpinner.getSelectedItem().toString();
        String section = sectionSpinner.getSelectedItem().toString();
        String role = "student";  // Set role to "student"

        if (lastname.isEmpty() || email.isEmpty() || password.isEmpty() || firstname.isEmpty() || middlename.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected registration year from the spinner
        String selectedYear = registrationYear.getSelectedItem().toString();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Send email verification
                            user.sendEmailVerification()
                                    .addOnCompleteListener(taskEmailVerification -> {
                                        if (taskEmailVerification.isSuccessful()) {
                                            // Verification email sent successfully
                                            Toast.makeText(RegisterActivity.this,
                                                    "Verification email sent to " + user.getEmail(),
                                                    Toast.LENGTH_SHORT).show();

                                            // Create a new User instance
                                            User newUser = User.createNewUser(lastname, firstname, middlename, idnum, email, password, course, section, role, selectedYear);

                                            // Set the registration year from the spinner
                                            newUser.setRegistrationYear(selectedYear);

                                            // Save user data to Firestore
                                            newUser.saveToFirestore(RegisterActivity.this, user);
                                        } else {
                                            // If email verification fails, delete the user and show an error message
                                            Toast.makeText(RegisterActivity.this,
                                                    "Failed to send verification email: " +
                                                            taskEmailVerification.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            user.delete();
                                        }

                                        progressBar.setVisibility(View.GONE);
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }



    // Callback methods from UserRegistrationCallback interface
    @Override
    public void onRegistrationSuccess() {
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        goToLoginActivity();
    }

    @Override
    public void onRegistrationFailure(String errorMessage) {
        Toast.makeText(this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}

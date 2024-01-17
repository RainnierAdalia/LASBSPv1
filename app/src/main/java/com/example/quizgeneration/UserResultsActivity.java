package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UserResultsActivity extends AppCompatActivity {

    private Button bsitButton;
    private Button bsisButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_results);

        bsitButton = findViewById(R.id.bsitButton);
        bsisButton = findViewById(R.id.bsisButton);

        bsitButton.setOnClickListener(v -> {
            // Handle navigation to BSIT user list
            Intent intent = new Intent(UserResultsActivity.this, BSITUserListActivity.class);
            intent.putExtra("course", "BSIT");
            intent.putExtra("registrationYear", getIntent().getStringExtra("registrationYear"));// Pass the selected batch as an extra
            startActivity(intent);
        });

        bsisButton.setOnClickListener(v -> {
            // Handle navigation to BSIS user list
            Intent intent = new Intent(UserResultsActivity.this, BSISUserListActivity.class);
            intent.putExtra("course", "BSIS"); // Pass the selected course as an extra
            intent.putExtra("registrationYear", getIntent().getStringExtra("registrationYear"));
            startActivity(intent);
        });

    }
}
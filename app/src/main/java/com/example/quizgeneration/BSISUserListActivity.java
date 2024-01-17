package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BSISUserListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsisuser_list);

        setupUI();
    }

    private void setupUI() {
        LinearLayout sectionButtonLayout = findViewById(R.id.sectionButtonLayout);

        // Add buttons dynamically for sections 1A, 1B, 1C
        addSectionButton(sectionButtonLayout, "1A");
        addSectionButton(sectionButtonLayout, "1B");
        addSectionButton(sectionButtonLayout, "1C");
        addSectionButton(sectionButtonLayout, "1D");
        addSectionButton(sectionButtonLayout, "1E");
        addSectionButton(sectionButtonLayout, "1F");
        addSectionButton(sectionButtonLayout, "1G");
    }

    private void addSectionButton(LinearLayout layout, final String section) {
        Button sectionButton = new Button(this);
        sectionButton.setText(section);
        sectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the selected section as an extra to the next activity
                navigateToUserList(section);
            }
        });
        layout.addView(sectionButton);
    }

    private void navigateToUserList(String section) {
        Intent intent = new Intent(this, DetailedUserListActivity.class);
        intent.putExtra("section", section);
        intent.putExtra("course", getIntent().getStringExtra("course"));
        intent.putExtra("registrationYear", getIntent().getStringExtra("registrationYear"));
        startActivity(intent);
    }

}

package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BatchSelectionActivity extends AppCompatActivity {

    private Spinner batchSpinner;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_selection);

        batchSpinner = findViewById(R.id.batchSpinner);
        nextButton = findViewById(R.id.nextButton);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch registration years from Firestore and populate the spinner
        db.collection("users")
                .orderBy("registrationYear")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> registrationYears = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Check if the document contains the 'registrationYear' field and it's a string
                            if (document.contains("registrationYear") && document.get("registrationYear") instanceof String) {
                                String registrationYear = document.getString("registrationYear");
                                if (!registrationYears.contains(registrationYear)) {
                                    registrationYears.add(registrationYear);
                                }
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                BatchSelectionActivity.this,
                                android.R.layout.simple_spinner_item,
                                registrationYears
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        batchSpinner.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBatch = batchSpinner.getSelectedItem().toString();

                // Handle navigation to UserResultsActivity with selected batch
                navigateToUserResults(selectedBatch);
            }
        });
    }

    private void navigateToUserResults(String registrationYear) {
        Intent intent = new Intent(BatchSelectionActivity.this, UserResultsActivity.class);
        intent.putExtra("registrationYear", registrationYear);
        startActivity(intent);
    }
}

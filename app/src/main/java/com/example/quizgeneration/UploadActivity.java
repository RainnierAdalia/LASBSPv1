package com.example.quizgeneration;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Button pdfUploadBtn = findViewById(R.id.pdfUploadBtn);
        pdfUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the UploadModuleActivity for PDF upload
                Intent intent = new Intent(UploadActivity.this, UploadModuleActivity.class);
                startActivity(intent);
            }
        });

        Button videoUploadBtn = findViewById(R.id.videoUploadBtn);
        videoUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the UploadVideoActivity for video upload
                Intent intent = new Intent(UploadActivity.this, UploadVideoActivity.class);
                startActivity(intent);
            }
        });
    }
}

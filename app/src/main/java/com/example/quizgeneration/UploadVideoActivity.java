package com.example.quizgeneration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadVideoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_UPLOAD_VIDEO = 302;
    private StorageReference storageRef;
    private Spinner chapterSpinner;
    private EditText videoTitleEditText;
    private int selectedChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        storageRef = FirebaseStorage.getInstance().getReference();
        videoTitleEditText = findViewById(R.id.videoTitleEditText);

        Button uploadVideoButton = findViewById(R.id.uploadVideoButton);
        chapterSpinner = findViewById(R.id.chapterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chapter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapterSpinner.setAdapter(adapter);

        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("UploadVideoActivity", "Upload video button clicked");

                selectedChapter = chapterSpinner.getSelectedItemPosition() + 1;

                // Start the video file picker intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, REQUEST_CODE_UPLOAD_VIDEO);
            }
        });
    }

    // Override onActivityResult for video upload logic
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPLOAD_VIDEO && resultCode == RESULT_OK && data != null) {
            // Handle the selected video data and upload it to Firebase Storage
            Uri videoUri = data.getData();
            uploadVideoToFirebase(videoUri);
        }
    }

    private void uploadVideoToFirebase(Uri videoUri) {
        Log.d("UploadVideoActivity", "Uploading video...");

        String videoTitle = videoTitleEditText.getText().toString().trim();

        if (videoTitle.isEmpty()) {
            Toast.makeText(this, "Video title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a reference to the location where the video will be stored in Firebase Storage
        StorageReference videoRef = storageRef.child("videos/chapter" + selectedChapter + "/" + videoTitle + ".mp4");

        // Upload the video to Firebase Storage
        UploadTask uploadTask = videoRef.putFile(videoUri);

        // Monitor the upload progress if needed
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Video upload success
                Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Video upload failure, handle accordingly
                Exception exception = task.getException();
                // Handle the exception
                Toast.makeText(this, "Error uploading video: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

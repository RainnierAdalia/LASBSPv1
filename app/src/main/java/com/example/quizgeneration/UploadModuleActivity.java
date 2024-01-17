package com.example.quizgeneration;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadModuleActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_UPLOAD = 301;
    private Button uploadButton;
    private Spinner chapterSpinner;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private int selectedChapter;
    private String title;
    private EditText moduleTitleEditText;

    // ActivityResultLauncher for file picking
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        uploadFile(result, title);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_module);

        storageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        chapterSpinner = findViewById(R.id.chapterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chapter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapterSpinner.setAdapter(adapter);

        uploadButton = findViewById(R.id.uploadButton);
        moduleTitleEditText = findViewById(R.id.moduleTitleEditText);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("UploadModuleActivity", "Upload button clicked");

                selectedChapter = chapterSpinner.getSelectedItemPosition() + 1;

                // Get the title from the EditText field
                title = moduleTitleEditText.getText().toString().trim(); // Assign to class-level variable

                // Check if permissions are granted before proceeding
                if (ContextCompat.checkSelfPermission(UploadModuleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Start the file picker intent
                    filePickerLauncher.launch("*/*");
                } else {
                    // Permissions not granted, request them using the new API
                    filePickerLauncher.launch("*/*");
                }
            }
        });

    }

    private void uploadFile(Uri fileUri, String title) {
        Log.d("UploadModuleActivity", "Uploading file...");

        String moduleTitle = moduleTitleEditText.getText().toString().trim();

        if (moduleTitle.isEmpty()) {
            Toast.makeText(this, "Module title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a reference to the location where the file will be stored in Firebase Storage
        StorageReference fileRef = storageRef.child("modules/chapter" + selectedChapter + "/" + moduleTitle + ".pdf");

        // Upload the file to Firebase Storage
        UploadTask uploadTask = fileRef.putFile(fileUri);

        // Monitor the upload progress and handle completion
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("UploadModuleActivity", "File uploaded successfully");

                // Upload success, get the download URL
                fileRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            // Determine the content type based on the file extension
                            String contentType = getContentTypeFromUri(fileUri);

                            // Store the download URL in Firebase Firestore
                            storeDownloadUrlInFirestore(downloadUri.toString(), title, contentType);
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure to get download URL
                            Toast.makeText(this, "Error getting download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Upload failure, handle accordingly
                Exception exception = task.getException();
                Toast.makeText(this, "Error uploading file: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getContentTypeFromUri(Uri uri) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        return fileExtension;
    }


    private void storeDownloadUrlInFirestore(String downloadUrl, String title, String contentType) {
        // Create a new document in Firestore to store the download URL
        db.collection("modules")
                .add(new Module(title, downloadUrl, contentType))
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error storing download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

package com.example.quizgeneration;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ModuleListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ModuleAdapter moduleAdapter;
    private List<Module> moduleList = new ArrayList<>();
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_list);

        storage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.moduleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moduleAdapter = new ModuleAdapter(moduleList);
        recyclerView.setAdapter(moduleAdapter);

        int selectedChapter = getIntent().getIntExtra("selectedChapter", 1);

        // Fetch modules for the selected chapter from Firebase Storage
        fetchModules(selectedChapter);
    }

    private void fetchModules(int selectedChapter) {
        // Create references to the locations where modules (PDFs) and videos are stored in Firebase Storage
        StorageReference pdfRef = storage.getReference("modules/chapter" + selectedChapter);
        StorageReference videoRef = storage.getReference("videos/chapter" + selectedChapter);

        // Clear the module list before fetching
        moduleList.clear();

        // Fetch PDFs
        pdfRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                String moduleName = item.getName();

                // Determine content type based on file extension
                String contentType = "pdf"; // Default value
                String extension = moduleName.substring(moduleName.lastIndexOf('.') + 1).toLowerCase();
                if ("pdf".equals(extension)) {
                    contentType = "pdf";
                }

                final String finalContentType = contentType;


                item.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    String downloadUrl = downloadUri.toString();

                    moduleList.add(new Module(moduleName, downloadUrl, finalContentType));
                    moduleAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> {
                    // Handle failure to get download URL
                });
            }
        }).addOnFailureListener(exception -> {
            // Handle error
        });

        // Fetch videos
        videoRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                String videoName = item.getName();

                // Determine content type based on file extension
                String contentType = "video"; // Default value
                String extension = videoName.substring(videoName.lastIndexOf('.') + 1).toLowerCase();
                if ("mp4".equals(extension) || "avi".equals(extension)) {
                    contentType = "video";
                }

                final String finalContentType = contentType;

                item.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    String downloadUrl = downloadUri.toString();

                    moduleList.add(new Module(videoName, downloadUrl, finalContentType));
                    moduleAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> {
                    // Handle failure to get download URL
                });
            }
        }).addOnFailureListener(exception -> {
            // Handle error
        });
    }


}

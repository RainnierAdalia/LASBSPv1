package com.example.quizgeneration;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<Module> moduleList;

    public ModuleAdapter(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        holder.moduleTitleTextView.setText(module.getTitle());
        holder.viewModuleButton.setOnClickListener(v -> {
            // Open the module (PDF or video) based on content type
            String contentType = module.getContentType();
            String downloadUrl = module.getDownloadUrl();
            openModuleWithUrl(holder.itemView.getContext(), downloadUrl, contentType);
        });
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView moduleTitleTextView;
        Button viewModuleButton;
        Context context;

        ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            moduleTitleTextView = itemView.findViewById(R.id.moduleTitleTextView);
            viewModuleButton = itemView.findViewById(R.id.viewModuleButton);
        }
    }

    private void openModuleWithUrl(Context context, String downloadUrl, String contentType) {
        if ("pdf".equalsIgnoreCase(contentType)) {
            // Handle PDFs
            StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl);

            // Get the actual download URL for the file
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Open PDF using the download URL
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(uri, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(pdfIntent);
            }).addOnFailureListener(e -> {
                // Handle failure to get download URL
                Toast.makeText(context, "Error getting download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        } else if ("video".equalsIgnoreCase(contentType)) {
            // Handle videos
            Intent videoIntent = new Intent(context, VideoViewerActivity.class);
            videoIntent.putExtra("videoUrl", downloadUrl);
            context.startActivity(videoIntent);
        } else {
            // Handle other supported types (e.g., docx)
            Intent docIntent = new Intent(Intent.ACTION_VIEW);
            docIntent.setData(Uri.parse(downloadUrl));
            docIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(docIntent);
        }
    }



}

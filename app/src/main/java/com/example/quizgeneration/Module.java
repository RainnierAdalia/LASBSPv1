package com.example.quizgeneration;

public class Module {
    private String title;
    private String downloadUrl;
    private String contentType;

    public Module(String title, String downloadUrl, String contentType) {
        this.title = title;
        this.downloadUrl = downloadUrl;
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getContentType() {
        return contentType;
    }
}

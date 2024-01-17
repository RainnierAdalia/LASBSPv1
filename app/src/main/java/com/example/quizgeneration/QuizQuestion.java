package com.example.quizgeneration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class QuizQuestion implements Parcelable {
    private String question;
    private Map<String, String> options;
    private String correctOptionLabel;
    private long timerDurationMillis; // Add this field
    private String id; // Add this field for question ID

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public QuizQuestion() {
        // Default constructor required for Firestore
    }

    public QuizQuestion(String question, Map<String, String> options, String correctOptionLabel) {
        this.question = question;
        this.options = options;
        this.correctOptionLabel = correctOptionLabel;
    }

    protected QuizQuestion(Parcel in) {
        question = in.readString();
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        options = new HashMap<>();
        options.putAll(bundleToMap(bundle.getBundle("options")));
        correctOptionLabel = in.readString();
        timerDurationMillis = in.readLong(); // Read the timer duration from the parcel
    }

    public static final Creator<QuizQuestion> CREATOR = new Creator<QuizQuestion>() {
        @Override
        public QuizQuestion createFromParcel(Parcel in) {
            return new QuizQuestion(in);
        }

        @Override
        public QuizQuestion[] newArray(int size) {
            return new QuizQuestion[size];
        }
    };

    // Getters and setters for private fields


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getCorrectOptionLabel() {
        return correctOptionLabel;
    }

    public void setCorrectOptionLabel(String correctOptionLabel) {
        this.correctOptionLabel = correctOptionLabel;
    }

    // Getter and setter for timer duration
    public long getTimerDurationMillis() {
        return timerDurationMillis;
    }

    public void setTimerDurationMillis(long timerDurationMillis) {
        this.timerDurationMillis = timerDurationMillis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        Bundle bundle = new Bundle();
        bundle.putBundle("options", mapToBundle(options));
        dest.writeBundle(bundle);
        dest.writeString(correctOptionLabel);
        dest.writeLong(timerDurationMillis); // Write the timer duration to the parcel
    }

    private Bundle mapToBundle(Map<String, String> map) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    private Map<String, String> bundleToMap(Bundle bundle) {
        Map<String, String> map = new HashMap<>();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                map.put(key, bundle.getString(key));
            }
        }
        return map;
    }
}

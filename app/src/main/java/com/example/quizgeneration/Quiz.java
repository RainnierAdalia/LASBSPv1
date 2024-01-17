package com.example.quizgeneration;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Quiz implements Parcelable {
    private String id;
    private String title;
    private List<String> questionIds;
    private String qrCodeImageUrl;
    private long timerDurationMillis;

    public Quiz() {
        // Empty constructor required for Firestore
    }

    public Quiz(String title, List<String> questionIds) {
        this.title = title;
        this.questionIds = questionIds;
    }

    // Add getter and setter for qrCodeImageUrl
    public String getQrCodeImageUrl() {
        return qrCodeImageUrl;
    }

    public void setQrCodeImageUrl(String qrCodeImageUrl) {
        this.qrCodeImageUrl = qrCodeImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Implement Parcelable methods
    protected Quiz(Parcel in) {
        id = in.readString();
        title = in.readString();
        questionIds = in.createStringArrayList();
        qrCodeImageUrl = in.readString();
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeStringList(questionIds);
        dest.writeString(qrCodeImageUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    public long getTimerDurationMillis() {
        return timerDurationMillis;
    }

    public void setTimerDurationMillis(long timerDurationMillis) {
        this.timerDurationMillis = timerDurationMillis;
    }
}

package com.example.quizgeneration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class User {

    public interface UserUpdateCallback {
        void onUserUpdateSuccess();

        void onUserUpdateFailure(String errorMessage);
    }

    private String userId;
    private String lastname; // Changed from username
    private String firstname;
    private String middlename;
    private Integer idnum;
    private String email;
    private String password;
    private String course;
    private String section;
    private String role;
    private String status;
    private String registrationYear; // Changed to String

    public User() {
        // Required empty constructor for Firestore deserialization
    }

    public User(String userId, String lastname, String firstname, String middlename, Integer idnum, String email, String password, String course, String section, String role, String registrationYear) {
        this.userId = userId;
        this.lastname = lastname;
        this.firstname = firstname;
        this.middlename = middlename;
        this.idnum = idnum;
        this.email = email;
        this.password = password;
        this.course = course;
        this.section = section;
        this.role = role;
        this.status = "pending";
        this.registrationYear = registrationYear;
    }

    public String getUserId() {
        return userId;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public Integer getIdnum() {
        return idnum;
    }
    public void setIdnum(int idnum) {
        this.idnum = idnum;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCourse() {
        return course;
    }

    public String getSection() {
        return section;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getRegistrationYear() {
        return registrationYear;
    }

    // Update the setRegistrationYear method to accept a String
    public void setRegistrationYear(String selectedYear) {
        this.registrationYear = selectedYear;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        if (role.equals("instructor")) {
            if (status.equals("approved")) {
                return "Approved Instructor";
            } else if (status.equals("pending")) {
                return "Pending Instructor";
            } else {
                return "Unknown";
            }
        } else if (role.equals("student")) {
            if (status.equals("approved")) {
                return "Approved Student";
            } else if (status.equals("pending")) {
                return "Pending Student";
            } else {
                return "Unknown";
            }
        } else {
            return "Unknown";
        }
    }

    public void updateUserData(UserUpdateCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("users").document(userId);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", status);

        userRef.update(updateMap)
                .addOnSuccessListener(aVoid -> {
                    callback.onUserUpdateSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onUserUpdateFailure("Failed to update user data: " + e.getMessage());
                });
    }

    private String getCurrentYearAsString() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public void saveToFirestore(UserRegistrationCallback callback, FirebaseUser firebaseUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = firebaseUser != null ? firebaseUser.getUid() : null;

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", uid);
        userMap.put("lastname", lastname);
        userMap.put("firstname", firstname);
        userMap.put("middlename", middlename);
        userMap.put("idnum", idnum);
        userMap.put("email", email);
        userMap.put("course", course);
        userMap.put("section", section);
        userMap.put("role", role);
        userMap.put("status", status);
        userMap.put("registrationYear", registrationYear);

        db.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    callback.onRegistrationSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onRegistrationFailure(e.getMessage());
                });
    }

    public interface UserRegistrationCallback {
        void onRegistrationSuccess();

        void onRegistrationFailure(String errorMessage);
    }

    public static User createNewUser(String lastname, String firstname, String middlename, Integer idnum, String email, String password, String course, String section, String role, String registrationYear) {
        return new User(null, lastname, firstname, middlename, idnum, email, password, course, section, role, registrationYear);
    }

}

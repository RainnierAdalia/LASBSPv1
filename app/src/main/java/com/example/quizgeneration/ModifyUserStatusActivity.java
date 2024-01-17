package com.example.quizgeneration;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ModifyUserStatusActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private List<User> pendingUsers = new ArrayList<>();
    private List<User> approvedAndRejectedUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_status);

        // Initialize UI elements
        userRecyclerView = findViewById(R.id.userRecyclerView);

        // Load user data from Firestore and populate lists
        loadUserData();
    }

    private void loadUserData() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming you have a "users" collection in Firestore
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear();
                            pendingUsers.clear();
                            approvedAndRejectedUsers.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);

                                if (user.getStatus().equals("pending")) {
                                    pendingUsers.add(user);
                                } else if (user.getStatus().equals("approved") || user.getStatus().equals("rejected")) {
                                    approvedAndRejectedUsers.add(user);
                                }
                            }

                            // Combine the lists in the desired order
                            List<User> combinedList = new ArrayList<>();
                            combinedList.addAll(pendingUsers);
                            combinedList.addAll(approvedAndRejectedUsers);

                            // Create the UserAdapter with the combined list and userActionListener
                            userAdapter = new UserAdapter(combinedList, ModifyUserStatusActivity.this);

                            // Set up the RecyclerView
                            userRecyclerView.setLayoutManager(new LinearLayoutManager(ModifyUserStatusActivity.this));
                            userRecyclerView.setAdapter(userAdapter);
                        } else {
                            // Handle errors while fetching data
                            showError("Error loading user data: " + task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void onAction(User user, UserAdapter.ActionType actionType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentStatus = user.getStatus();

        if (actionType == UserAdapter.ActionType.APPROVE) {
            if (currentStatus.equals("pending")) {
                user.setStatus("approved");
                db.collection("users").document(user.getUserId())
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    userAdapter.notifyDataSetChanged();
                                } else {
                                    showError("Error updating status: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        } else if (actionType == UserAdapter.ActionType.REJECT) {
            if (currentStatus.equals("pending")) {
                user.setStatus("rejected");
                db.collection("users").document(user.getUserId())
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    userAdapter.notifyDataSetChanged();
                                } else {
                                    showError("Error updating status: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        } else if (actionType == UserAdapter.ActionType.REMOVE) {
            if (currentStatus.equals("approved") || currentStatus.equals("rejected")) {
                user.setStatus("pending");
                db.collection("users").document(user.getUserId())
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    userAdapter.notifyDataSetChanged();
                                } else {
                                    showError("Error updating status: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        }
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}

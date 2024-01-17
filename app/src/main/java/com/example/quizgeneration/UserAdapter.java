package com.example.quizgeneration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener userActionListener;

    public UserAdapter(List<User> userList, OnUserActionListener userActionListener) {
        this.userList = userList;
        this.userActionListener = userActionListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, courseTextView, sectionTextView, roleTextView, statusTextView, emailTextView;
        private Button approveButton, rejectButton, removeButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.usernameTextView);  // Adjust this line
            emailTextView = itemView.findViewById(R.id.emailTextView);
            courseTextView = itemView.findViewById(R.id.courseTextView);
            sectionTextView = itemView.findViewById(R.id.sectionTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }


        public void bind(User user) {
            // Display user information
            String fullName = user.getFirstname() + " " + user.getMiddlename() + " " + user.getLastname();
            nameTextView.setText("Name: " + fullName);

            courseTextView.setText("Course: " + user.getCourse());
            emailTextView.setText("Email: " + user.getEmail());
            sectionTextView.setText("Section: " + user.getSection());
            roleTextView.setText("Role: " + user.getRole());
            statusTextView.setText("Status: " + user.getStatus());

            if (user.getStatus().equals("pending")) {
                approveButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                removeButton.setVisibility(View.GONE);

                approveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userActionListener.onAction(user, ActionType.APPROVE);
                    }
                });

                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userActionListener.onAction(user, ActionType.REJECT);
                    }
                });
            } else if (user.getStatus().equals("approved") || user.getStatus().equals("rejected")) {
                approveButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
                removeButton.setVisibility(View.VISIBLE);

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userActionListener.onAction(user, ActionType.REMOVE);
                    }
                });
            } else {
                // Handle other cases if needed
                approveButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
                removeButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnUserActionListener {
        void onAction(User user, ActionType actionType);
    }

    public enum ActionType {
        APPROVE,
        REJECT,
        REMOVE
    }
}

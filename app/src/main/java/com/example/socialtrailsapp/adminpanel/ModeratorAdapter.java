package com.example.socialtrailsapp.adminpanel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ModeratorAdapter extends RecyclerView.Adapter<ModeratorAdapter.ModeratorViewHolder> {

    private List<Users> moderatorList;
    private Context context;
    private UserService userService;

    public ModeratorAdapter(Context context, List<Users> moderators) {
        this.context = context;
        this.moderatorList = moderators;
        this.userService = new UserService();
    }

    @NonNull
    @Override
    public ModeratorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.moderators, parent, false);
        return new ModeratorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModeratorViewHolder holder, int position) {
        Users moderator = moderatorList.get(position);


        if (moderator != null) {
            Log.d("ModeratorAdapter", "Binding moderator: " + moderator.getUsername());
            holder.moderatorNumber.setText("Moderator " + (position + 1));
            holder.moderatorName.setText(moderator.getUsername() != null ? moderator.getUsername() : "N/A");
            holder.moderatorEmail.setText(moderator.getEmail() != null ? moderator.getEmail() : "N/A");
        }


        holder.removeButton.setOnClickListener(v -> {
            removeModerator(moderator.getUserId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return moderatorList.size();
    }

    // ViewHolder class to hold references to the views
    static class ModeratorViewHolder extends RecyclerView.ViewHolder {
        TextView moderatorNumber, moderatorName, moderatorEmail;
        Button removeButton;

        public ModeratorViewHolder(@NonNull View itemView) {
            super(itemView);
            moderatorNumber = itemView.findViewById(R.id.txtModeratorNumber);
            moderatorName = itemView.findViewById(R.id.txtmoderatorName);
            moderatorEmail = itemView.findViewById(R.id.txtmoderatorEmail);
            removeButton = itemView.findViewById(R.id.removemoderator);
        }
    }


    private void removeModerator(String moderatorId, int position) {

        userService.deleteProfile(moderatorId, new OperationCallback() {
            @Override
            public void onSuccess() {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(moderatorId);
                ref.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        moderatorList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, moderatorList.size());
                        Toast.makeText(context, "Moderator removed successfully", Toast.LENGTH_SHORT).show();
                    } else {

                        Log.e("ModeratorAdapter", "Failed to remove moderator from Realtime Database: " + task.getException());
                        Toast.makeText(context, "Failed to remove moderator from Realtime Database", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("ModeratorAdapter", "Failed to remove moderator: " + error);
                Toast.makeText(context, "Failed to remove moderator: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

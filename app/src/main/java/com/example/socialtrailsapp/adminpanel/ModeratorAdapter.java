package com.example.socialtrailsapp.adminpanel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.SignInActivity;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.Utility.Utils;
import com.example.socialtrailsapp.userSettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        if (moderator.getProfilepicture() != null && !moderator.getProfilepicture().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(moderator.getProfilepicture())
                    .placeholder(R.drawable.user)  // Use a default image if no profile picture is provided
                    .into(holder.imgProfilePicture);
        } else {
            holder.imgProfilePicture.setImageResource(R.drawable.user);  // Default image if null
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
      //  Button removeButton;
        ImageView imgProfilePicture,removeButton;
        public ModeratorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfilePicture = itemView.findViewById(R.id.imgProfilePicture);
            moderatorNumber = itemView.findViewById(R.id.txtModeratorNumber);
            moderatorName = itemView.findViewById(R.id.txtmoderatorName);
            moderatorEmail = itemView.findViewById(R.id.txtmoderatorEmail);
            removeButton = itemView.findViewById(R.id.removemoderator);
        }
    }


    private void removeModerator(String moderatorId, int position) {

        new AlertDialog.Builder(context)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete this moderator?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    userService.deleteProfile(moderatorId, new OperationCallback() {
                        @Override
                        public void onSuccess() {
                            moderatorList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, moderatorList.size());
                            Toast.makeText(context, "Moderator removed successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e("ModeratorAdapter", "Failed to remove moderator: " + error);
                            Toast.makeText(context, "Failed to remove moderator: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();

    }
}

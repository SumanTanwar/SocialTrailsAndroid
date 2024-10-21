package com.example.socialtrailsapp.Utility;

import com.example.socialtrailsapp.ModelData.UserFollow;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Context;
import android.widget.Toast;

public class FollowService {
    private DatabaseReference databaseReference;
    private Context context;

    public FollowService(Context context) {
        this.context = context; // Properly initialize context
        databaseReference = FirebaseDatabase.getInstance().getReference("userfollow");
    }

    public void addFollow(UserFollow userFollow) {
        String followId = databaseReference.push().getKey();
        userFollow.setFollowId(followId);
        databaseReference.child(followId).setValue(userFollow)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Followed successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to follow: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void removeFollow(String followId) {
        databaseReference.child(followId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Unfollowed successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to unfollow: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

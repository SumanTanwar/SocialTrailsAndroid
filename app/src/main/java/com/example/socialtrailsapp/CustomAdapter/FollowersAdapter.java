package com.example.socialtrailsapp.CustomAdapter;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.net.Uri;
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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.FollowUnfollowActivity;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.SessionManager;

import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {
    private Context context;
    private List<Users> followersUserList;
    private OnFollowerClickListener clickListener;

    private SessionManager sessionManager;
    private FollowService followService;

    public FollowersAdapter(Context context, List<Users> followersUserList, OnFollowerClickListener clickListener) {
        this.context = context;
        this.followersUserList = followersUserList;
        this.clickListener = clickListener;
        sessionManager = SessionManager.getInstance(context);
        followService = new FollowService();
    }

    @NonNull
    @Override
    public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower_user, parent, false);
        return new FollowersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersViewHolder holder, int position) {
        Users user = followersUserList.get(position);
        if (user != null) {
            holder.usernameTextView.setText(user.getUsername());

            if (user.getProfilepicture() != null) {
                Uri profileImageUri = Uri.parse(user.getProfilepicture());
                Glide.with(context)
                        .load(profileImageUri)
                        .transform(new CircleCrop())
                        .into(holder.profileImageView);
            } else {
                Glide.with(context)
                        .load(R.drawable.user)
                        .transform(new CircleCrop())
                        .into(holder.profileImageView);
            }

            if (sessionManager.getroleType().equals(UserRole.ADMIN.getRole()) || sessionManager.getroleType().equals(UserRole.MODERATOR.getRole())) {
                holder.followButton.setVisibility(View.GONE);
                holder.removeButton.setVisibility(View.VISIBLE);
            }

            else {
                String currentUserId = sessionManager.getUserID();
                checkFollowBack(currentUserId, user.getUserId(), holder);

            }


            holder.followButton.setOnClickListener(v -> {

                String currentUserId = sessionManager.getUserID();
                followService.sendFollowRequest(currentUserId, user.getUserId(), new OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Follow request sent!", Toast.LENGTH_SHORT).show();
                        holder.followButton.setVisibility(View.GONE);
                        holder.removeButton.setVisibility(View.GONE);
                        // unfollow button show
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

            });

            holder.removeButton.setOnClickListener(v -> {
                clickListener.onRemoveClick(position);
            });

        }
    }

    @Override
    public int getItemCount() {
        return followersUserList != null ? followersUserList.size() : 0;
    }

    public class FollowersViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView profileImageView;
        Button followButton;
        Button removeButton;

        public FollowersViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userIdTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            followButton = itemView.findViewById(R.id.followButton);
            removeButton = itemView.findViewById(R.id.removeButton);
            itemView.setOnClickListener(v -> clickListener.onFollowerClick(getAdapterPosition()));
        }
    }

    public interface OnFollowerClickListener {
        void onFollowerClick(int position);

        void onRemoveClick(int position);
    }
    private void checkFollowBack(String currentUserId, String userIdToCheck, FollowersViewHolder holder) {
        followService.checkPendingRequestsForCancel(currentUserId, userIdToCheck, new DataOperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean hasPendingRequest) {
                if (hasPendingRequest) {
                    holder.followButton.setVisibility(View.GONE);
                    holder.removeButton.setVisibility(View.GONE);
                } else {
                    holder.followButton.setVisibility(View.VISIBLE);
                    holder.removeButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String error) {
                // Handle error (optional: you can set a default UI state)
                holder.followButton.setVisibility(View.VISIBLE);
                holder.removeButton.setVisibility(View.GONE);
            }
        });
    }

}


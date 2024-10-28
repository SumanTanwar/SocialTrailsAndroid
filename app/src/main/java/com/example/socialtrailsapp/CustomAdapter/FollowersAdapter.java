package com.example.socialtrailsapp.CustomAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;

import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {

    private List<Users> followersUserList;
    private OnFollowerClickListener clickListener;

    private static final String ADMIN_EMAIL = "socialtrails2024@gmail.com";


    public FollowersAdapter(List<Users> followersUserList, OnFollowerClickListener clickListener) {
        this.followersUserList = followersUserList;
        this.clickListener = clickListener;
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
            Glide.with(holder.profileImageView.getContext())
                    .load(user.getProfilepicture()) // Assuming the method exists in your model
                    .placeholder(R.drawable.user) // Placeholder image
                    .into(holder.profileImageView);

            if (ADMIN_EMAIL.equals(user.getEmail())) {
                holder.followButton.setVisibility(View.GONE);
                holder.removeButton.setVisibility(View.VISIBLE);
            } else {
                holder.followButton.setVisibility(View.VISIBLE);
                holder.removeButton.setVisibility(View.GONE);
            }

            holder.followButton.setOnClickListener(v -> {
                clickListener.onFollowClick(position);
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
        void onFollowClick(int position);
        void onRemoveClick(int position);
    }
}


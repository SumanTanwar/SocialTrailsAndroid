package com.example.socialtrailsapp.CustomAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        }
    }

    @Override
    public int getItemCount() {
        return followersUserList != null ? followersUserList.size() : 0;
    }

    public class FollowersViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView profileImageView;

        public FollowersViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userIdTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            itemView.setOnClickListener(v -> clickListener.onFollowerClick(getAdapterPosition()));
        }
    }

    public interface OnFollowerClickListener {
        void onFollowerClick(int position);
    }
}

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

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {

    private List<Users> followingUserList;
    private OnFollowingClickListener clickListener;

    public FollowingAdapter(List<Users> followingUserList, OnFollowingClickListener clickListener) {
        this.followingUserList = followingUserList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_user, parent, false);
        return new FollowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position) {
        Users user = followingUserList.get(position);
        if (user != null) {
            holder.usernameTextView.setText(user.getUsername());
            Glide.with(holder.profileImageView.getContext())
                    .load(user.getProfilepicture())
                    .placeholder(R.drawable.user) // Placeholder image
                    .into(holder.profileImageView);
        }
    }

    @Override
    public int getItemCount() {
        return followingUserList != null ? followingUserList.size() : 0;
    }

    public class FollowingViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView profileImageView;

        public FollowingViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userIdTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            itemView.setOnClickListener(v -> clickListener.onFollowingClick(getAdapterPosition()));
        }
    }

    public interface OnFollowingClickListener {
        void onFollowingClick(int position);
    }
}

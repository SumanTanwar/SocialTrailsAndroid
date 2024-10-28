package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.SessionManager;

import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {

    private List<Users> followingUserList;
    private OnFollowingClickListener clickListener;

    private SessionManager sessionManager;
    private Context context;
    public FollowingAdapter(Context context, List<Users> followingUserList, OnFollowingClickListener clickListener) {
        this.followingUserList = followingUserList;
        this.clickListener = clickListener;
        sessionManager = SessionManager.getInstance(context);
        this.context = context;
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
                holder.removeButton.setVisibility(View.VISIBLE);
                holder.removeButton.setOnClickListener(v -> clickListener.onRemoveClick(position));
            }
            else
            {
                holder.removeButton.setVisibility(View.GONE);

            }

        }
    }

    @Override
    public int getItemCount() {
        return followingUserList != null ? followingUserList.size() : 0;
    }

    public class FollowingViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView profileImageView;
        Button removeButton;
      //  Button unfollowButton;

        public FollowingViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userIdTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            removeButton = itemView.findViewById(R.id.removeButton);
           // unfollowButton = itemView.findViewById(R.id.unFollowButton);
            itemView.setOnClickListener(v -> clickListener.onFollowingClick(getAdapterPosition()));
        }
    }

    public interface OnFollowingClickListener {
        void onFollowingClick(int position);
        void onRemoveClick(int position);

    }
}

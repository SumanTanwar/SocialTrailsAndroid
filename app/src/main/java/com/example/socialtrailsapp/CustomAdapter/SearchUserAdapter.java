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

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder> {

    private List<Users> userList;
    private OnTextClickListener onTextClickListener;

    public interface OnTextClickListener {
        void redirectToProfilePage(int position);
    }

    public SearchUserAdapter(List<Users> userList, OnTextClickListener onTextClickListener) {
        this.userList = userList;
        this.onTextClickListener = onTextClickListener;
    }

    @NonNull
    @Override
    public SearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false); // Use your simple layout here
        return new SearchUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserViewHolder holder, int position) {
        Users user = userList.get(position);

        // Set user details
        holder.txtUserName.setText(user.getUsername());

        // Load profile picture (if available)
        if (user.getProfilepicture() != null && !user.getProfilepicture().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfilepicture())
                    .placeholder(R.drawable.user)
                    .into(holder.imgProfilePicture);
        } else {
            holder.imgProfilePicture.setImageResource(R.drawable.user); // Default image if null
        }

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && onTextClickListener != null) {
                onTextClickListener.redirectToProfilePage(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class SearchUserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfilePicture;
        TextView txtUserName;

        public SearchUserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfilePicture = itemView.findViewById(R.id.profileImageView); // Ensure this matches your layout
            txtUserName = itemView.findViewById(R.id.usernameTextView); // Ensure this matches your layout
        }
    }
}

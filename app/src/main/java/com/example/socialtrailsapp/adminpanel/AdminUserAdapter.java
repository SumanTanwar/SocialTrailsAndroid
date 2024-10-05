package com.example.socialtrailsapp.adminpanel;

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

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder> {

    private List<Users> userList;
    private OnTextClickListener onTextClickListener;

    public interface OnTextClickListener {
        void redirectToProfilePage(int position);
    }

    public AdminUserAdapter(List<Users> userList,OnTextClickListener onTextClickListener) {
        this.userList = userList;
        this.onTextClickListener = onTextClickListener;
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_users, parent, false);  // Assuming your XML layout is named item_admin_user.xml
        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        Users user = userList.get(position);

        // Set user details
        holder.txtUserName.setText(user.getUsername());
        holder.txtEmail.setText(user.getEmail());
        holder.txtRegisteredDate.setText("Registered on: " + user.getCreatedon());

        // Load profile picture (if available)
        if (user.getProfilepicture() != null && !user.getProfilepicture().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfilepicture())
                    .placeholder(R.drawable.user)  // Use a default image if no profile picture is provided
                    .into(holder.imgProfilePicture);
        } else {
            holder.imgProfilePicture.setImageResource(R.drawable.user);  // Default image if null
        }
        holder.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onTextClickListener != null) {
                    onTextClickListener.redirectToProfilePage(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class AdminUserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfilePicture;
        TextView txtUserName, txtEmail, txtRegisteredDate;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfilePicture = itemView.findViewById(R.id.imgProfilePicture);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtRegisteredDate = itemView.findViewById(R.id.txtRegisteredDate);
            // Removed btnSuspend and btnDelete as they are no longer needed
        }
    }
}

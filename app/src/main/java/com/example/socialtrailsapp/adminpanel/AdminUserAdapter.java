package com.example.socialtrailsapp.adminpanel;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
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

        if(user.getProfiledeleted() || user.getAdmindeleted())
        {
            holder.txtStatus.setTextColor(Color.WHITE);
            holder.txtStatus.setBackgroundColor(Color.RED);
            holder.txtStatus.setText("Deleted");

        }
        else if(user.getSuspended())
        {
            holder.txtStatus.setTextColor(Color.WHITE);
            holder.txtStatus.setBackgroundColor(Color.parseColor("#FF9800"));
            holder.txtStatus.setText("Suspended");
        }
        else
        {
            holder.txtStatus.setVisibility(View.GONE);
        }
        // Load profile picture (if available)

        if (user.getProfilepicture() != null && !user.getProfilepicture().isEmpty()) {
            Uri profileImageUri = Uri.parse(user.getProfilepicture());
            Glide.with(holder.itemView.getContext())
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(holder.imgProfilePicture);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.user)
                    .transform(new CircleCrop())
                    .into(holder.imgProfilePicture);
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
        TextView txtUserName, txtEmail, txtRegisteredDate,txtStatus;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfilePicture = itemView.findViewById(R.id.imgProfilePicture);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtRegisteredDate = itemView.findViewById(R.id.txtRegisteredDate);
            // Removed btnSuspend and btnDelete as they are no longer needed
        }
    }
}

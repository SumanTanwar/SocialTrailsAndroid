package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
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
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notificationList;
    private Context context;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        // Set notification message
        holder.txtNotificationMessage.setText(notification.getMessage());

        // Load the profile picture using Glide
        String profileImageUrl = notification.getFollowerProfilePic();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Uri profileImageUri = Uri.parse(profileImageUrl);
            Glide.with(context)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(holder.profilePic);
        } else {
            Glide.with(context)
                    .load(R.drawable.user) // Default image
                    .transform(new CircleCrop())
                    .into(holder.profilePic);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView txtNotificationMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_notify);
            txtNotificationMessage = itemView.findViewById(R.id.txt_notification_message);
        }
    }
}

package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.socialtrailsapp.FollowUnfollowActivity;
import com.example.socialtrailsapp.MainActivity;
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.ModelData.ReportType;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.UserPostDetailActivity;
import com.example.socialtrailsapp.adminpanel.AdminUserViewActivity;

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



        // Load the profile picture using Glide
        String profileImageUrl = notification.getUserprofilepicture();
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


        // Set notification message
        holder.txtNotificationMessage.setText(notification.getUsername() +  notification.getMessage());
        holder.txtNotificationMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.getType().equalsIgnoreCase("post")) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("postdetailId", notification.getRelatedId()); // Pass the reportId as post ID
                    context.startActivity(intent);
                } else  {
                    Intent intent = new Intent(context, FollowUnfollowActivity.class);
                    intent.putExtra("intentuserId", notification.getRelatedId()); // Pass the reportId as user ID
                    context.startActivity(intent);
                }
            }
        });
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

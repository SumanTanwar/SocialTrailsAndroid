package com.example.socialtrailsapp.CustomAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.Utils;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.PostViewHolder> {

    private Context context;
    private List<UserPost> postList;
    private SessionManager sessionManager;

    public UserPostAdapter(Context context, List<UserPost> postList) {
        this.context = context;
        this.postList = postList;
        sessionManager = SessionManager.getInstance(context);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        UserPost post = postList.get(position);

        if (sessionManager.getProfileImage() != null) {
            Uri profileImageUri = Uri.parse(sessionManager.getProfileImage());
            Glide.with(context)
                    .load(profileImageUri)
                    .transform(new com.bumptech.glide.load.resource.bitmap.CircleCrop())
                    .into(holder.userProfileImage);

        } else {
            Glide.with(context)
                    .load(R.drawable.user)
                    .transform(new CircleCrop())
                    .into(holder.userProfileImage);

        }

        holder.userName.setText(sessionManager.getUsername());

        holder.userLocation.setText(post.getLocation());
        holder.postCaption.setText(post.getCaptiontext());
        holder.detailrelativetime.setText(Utils.getRelativeTime(post.getCreatedon()));

        holder.imagesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        PostImageAdapter imageAdapter = new PostImageAdapter(context, post.getUploadedImageUris());
        holder.imagesRecyclerView.setAdapter(imageAdapter);

        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, post.getPostId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage;
        TextView userName;
        TextView userLocation;
        TextView postCaption;
        TextView detailrelativetime;
        RecyclerView imagesRecyclerView;
        ImageButton optionsButton;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userName);
            userLocation = itemView.findViewById(R.id.userLocation);
            postCaption = itemView.findViewById(R.id.postCaption);
            detailrelativetime = itemView.findViewById(R.id.detailrelativetime);
            imagesRecyclerView = itemView.findViewById(R.id.imagesRecyclerView);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }
    }
    private void showPopupMenu(View view, String postId) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.postoptions_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.edit_option)
            {
                editPost(postId);
                return true;
            }
            else  if(item.getItemId() == R.id.delete_option)
            {
                confirmDelete(postId);
                return true;
            }
           else
            {
                    return false;
            }
        });
        popupMenu.show();
    }
    private void editPost(String postId) {
        Toast.makeText(context,"edit postid" + postId,Toast.LENGTH_SHORT).show();

    }

    private void confirmDelete(String postId) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deletePost(postId);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deletePost(String postId) {
        Toast.makeText(context,"edit postid" + postId,Toast.LENGTH_SHORT).show();
    }
}

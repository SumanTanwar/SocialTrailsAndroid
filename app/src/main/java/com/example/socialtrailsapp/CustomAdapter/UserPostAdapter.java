package com.example.socialtrailsapp.CustomAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.LikeResult;
import com.example.socialtrailsapp.ModelData.PostLike;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.UserPostEditActivity;
import com.example.socialtrailsapp.Utility.PostLikeService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.Utils;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.PostViewHolder> {

    private Context context;
    private List<UserPost> postList;
    private SessionManager sessionManager;
    UserPostService userPostService;
    PostLikeService postLikeService;

    public UserPostAdapter(Context context, List<UserPost> postList) {
        this.context = context;
        this.postList = postList;
        sessionManager = SessionManager.getInstance(context);
        userPostService = new UserPostService();
        postLikeService = new PostLikeService();
        setHasStableIds(true);
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
                    .transform(new CircleCrop())
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

        holder.optionsButton.setOnClickListener(view -> showPopupMenu(view, post.getPostId(), holder.getAdapterPosition()));

        String userId = sessionManager.getUserID();
        //update the like
        postLikeService.getPostLikeByUserandPostId(post.getPostId(), userId, new DataOperationCallback<PostLike>() {
            @Override
            public void onSuccess(PostLike postLike) {
                post.setLiked(true);
                holder.postlikeButton.setImageResource(R.drawable.heart_red);
                holder.postlikecnt.setText(String.valueOf(post.getLikecount()));

            }

            @Override
            public void onFailure(String error) {
                post.setLiked(false);
                holder.postlikeButton.setImageResource(R.drawable.like);
                holder.postlikecnt.setText(String.valueOf(post.getLikecount()));

            }
        });

        holder.postlikeButton.setOnClickListener(view -> {
            Log.d("post like", "click");
            view.requestFocus();
            postLike(holder, post.getPostId(),position);
        });

        holder.postlikecnt.setText(String.valueOf(post.getLikecount()));
    }
    @Override
    public long getItemId(int position) {
        return postList.get(position).getPostId().hashCode();
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
        TextView detailrelativetime, postlikecnt;
        RecyclerView imagesRecyclerView;
        ImageButton optionsButton, postlikeButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userName);
            userLocation = itemView.findViewById(R.id.userLocation);
            postCaption = itemView.findViewById(R.id.postCaption);
            detailrelativetime = itemView.findViewById(R.id.detailrelativetime);
            imagesRecyclerView = itemView.findViewById(R.id.imagesRecyclerView);
            optionsButton = itemView.findViewById(R.id.optionsButton);
            postlikeButton = itemView.findViewById(R.id.postlikeButton);
            postlikecnt = itemView.findViewById(R.id.postlikecnt);
        }
    }

    private void showPopupMenu(View view, String postId, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.postoptions_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.edit_option) {
                editPost(postId);
                return true;
            } else if (item.getItemId() == R.id.delete_option) {
                confirmDelete(postId, position);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void editPost(String postId) {
        Intent intent = new Intent(context, UserPostEditActivity.class);
        intent.putExtra("postdetailId", postId);
        context.startActivity(intent);
    }

    private void confirmDelete(String postId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deletePost(postId, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deletePost(String postId, int position) {
        userPostService.deleteUserPost(postId, new OperationCallback() {
            @Override
            public void onSuccess() {
                postList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(context, "Post delete failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postLike(PostViewHolder holder, String postId,int position) {
        String userId = sessionManager.getUserID();
        postLikeService.likeandUnlikePost(postId, userId, new DataOperationCallback<LikeResult>() {
            @Override
            public void onSuccess(LikeResult data) {
                UserPost post = postList.get(position);
                post.setLiked(data.isLike());
                post.setLikecount(data.getCount());

                holder.postlikecnt.setText(String.valueOf(data.getCount()));
                holder.postlikeButton.setImageResource(data.isLike() ? R.drawable.heart_red : R.drawable.like);

            }

            @Override
            public void onFailure(String error) {

            }
        });


    }
}

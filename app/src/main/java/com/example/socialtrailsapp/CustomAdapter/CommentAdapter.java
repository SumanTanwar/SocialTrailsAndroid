package com.example.socialtrailsapp.CustomAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.PostComment;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.PostCommentService;
import com.example.socialtrailsapp.Utility.SessionManager;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    public interface CommentActionListener {
        void onCommentDeleted(String postId);
    }

    private Context context;
    private List<PostComment> comments;
    private SessionManager sessionManager;
    private PostCommentService postCommentService;
    private CommentActionListener actionListener;
    private String postId;

    public CommentAdapter(Context context, List<PostComment> comments, String postId, CommentActionListener actionListener) {
        this.context = context;
        this.comments = comments;
        this.postId = postId;
        this.sessionManager = SessionManager.getInstance(context);
        this.postCommentService = new PostCommentService();
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        PostComment comment = comments.get(position);
        holder.commentText.setText(comment.getCommenttext());
        Log.d("commentadap","username :" + comment.getUsername());
        holder.cmtuserName.setText(comment.getUsername());


        if (comment.getUserprofilepicture() != null) {
            Uri profileImageUri = Uri.parse(comment.getUserprofilepicture());
            Glide.with(context)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(holder.cmtuserProfileImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.user)
                    .transform(new CircleCrop())
                    .into(holder.cmtuserProfileImage);
        }

        String currentUserId = sessionManager.getUserID();
        holder.cmtdeleteButton.setVisibility(View.GONE);
        if(sessionManager.getroleType().equals(UserRole.USER.getRole()) && comment.getUserId().equals(currentUserId))
        {
            holder.cmtdeleteButton.setVisibility(View.VISIBLE);
        } else if (sessionManager.getroleType().equals(UserRole.ADMIN.getRole()) || sessionManager.getroleType().equals(UserRole.MODERATOR.getRole())) {
            holder.cmtdeleteButton.setVisibility(View.VISIBLE);
        }
        else {
            holder.cmtdeleteButton.setVisibility(View.GONE); // Hide delete button
        }

        // Handle delete button click
        holder.cmtdeleteButton.setOnClickListener(v -> {
            confirmDelete(comment.getPostcommentId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void updateComments(List<PostComment> newComments) {
        this.comments.clear();
        this.comments.addAll(newComments);
        notifyDataSetChanged();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView cmtuserProfileImage;
        TextView cmtuserName;
        TextView commentText;
        ImageButton cmtdeleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            cmtuserProfileImage = itemView.findViewById(R.id.cmtuserProfileImage);
            cmtuserName = itemView.findViewById(R.id.cmtuserName);
            commentText = itemView.findViewById(R.id.commentText);
            cmtdeleteButton = itemView.findViewById(R.id.cmtdeleteButton);
        }
    }
    private void confirmDelete(String commentId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteComment(commentId, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteComment(String commentId, int position) {
        postCommentService.removePostComment(commentId, new OperationCallback() {
            @Override
            public void onSuccess() {
                comments.remove(position);
                notifyItemRemoved(position);
                if (actionListener != null) {
                    actionListener.onCommentDeleted(postId);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to delete comments", Toast.LENGTH_SHORT).show();
            }
        });

    }

}


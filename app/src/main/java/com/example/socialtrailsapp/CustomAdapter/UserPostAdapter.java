package com.example.socialtrailsapp.CustomAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.socialtrailsapp.FollowUnfollowActivity;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.LikeResult;
import com.example.socialtrailsapp.ModelData.PostComment;
import com.example.socialtrailsapp.ModelData.PostLike;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.ModelData.ReportType;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.SearchUserActivity;
import com.example.socialtrailsapp.UserPostEditActivity;
import com.example.socialtrailsapp.Utility.MapLocationPinDialog;
import com.example.socialtrailsapp.Utility.PostCommentService;
import com.example.socialtrailsapp.Utility.PostLikeService;
import com.example.socialtrailsapp.Utility.ReportService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.FragmentActivity;
public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.PostViewHolder> {

    private Context context;
    private List<UserPost> postList;
    private SessionManager sessionManager;
    UserPostService userPostService;
    PostLikeService postLikeService;
    PostCommentService postCommentService;
    TextView noCommentsTextView;
    ReportService repostService;
    private boolean isFromDashboard;

    public UserPostAdapter(Context context, List<UserPost> postList, boolean isFromDashboard) {
        this.context = context;
        this.postList = postList;
        sessionManager = SessionManager.getInstance(context);
        userPostService = new UserPostService();
        postLikeService = new PostLikeService();
        postCommentService = new PostCommentService();
        repostService = new ReportService();
        this.isFromDashboard = isFromDashboard;
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
        String userId = sessionManager.getUserID();

        if (post.getUserprofilepicture() != null) {
            Uri profileImageUri = Uri.parse(post.getUserprofilepicture());
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

        holder.userName.setText(post.getUsername());
        holder.userLocation.setText(post.getLocation());
        // holder.userLocation.setText("CN Tower, Toronto, Ontario");
        holder.userLocation.setOnClickListener(view -> {

//            double latitude = 43.6426;
//            double longitude = -79.3871;

            double latitude = post.getLatitude();
            double longitude = post.getLongitude();
            LatLng location = new LatLng(latitude, longitude);
            MapLocationPinDialog mapDialog = new MapLocationPinDialog((FragmentActivity) context, location, post.getLocation());
            mapDialog.show();
        });


        holder.postCaption.setText(post.getCaptiontext());
        holder.detailrelativetime.setText(Utils.getRelativeTime(post.getCreatedon()));

        holder.imagesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        PostImageAdapter imageAdapter = new PostImageAdapter(context, post.getUploadedImageUris());
        holder.imagesRecyclerView.setAdapter(imageAdapter);
        if (post.getUserId().equals(userId)) {
            holder.optionsButton.setVisibility(View.VISIBLE);
        } else {
            holder.optionsButton.setVisibility(View.GONE);
        }
        holder.optionsButton.setOnClickListener(view -> showPopupMenu(view, post.getPostId(), holder.getAdapterPosition()));


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
        holder.postlikecnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLikesDialog(post.getPostId());
            }
        });
        //comment
        holder.cmtpostcnt.setText(String.valueOf(post.getCommentcount()));
        holder.commentButton.setOnClickListener(view -> {
            openCommentDialog(post.getPostId(),post.getUserId());
        });

        // Report
        if (post.getUserId().equals(userId)) {
            holder.postreport.setVisibility(View.GONE);
        } else {
            holder.postreport.setVisibility(View.VISIBLE);
        }
        holder.postreport.setOnClickListener(view -> {
            openReportDialog(post.getPostId());
        });

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
        TextView detailrelativetime, postlikecnt,cmtpostcnt;
        RecyclerView imagesRecyclerView;
        ImageButton optionsButton, postlikeButton,commentButton,postreport;

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
            cmtpostcnt = itemView.findViewById(R.id.cmtpostcnt);
            commentButton = itemView.findViewById(R.id.commentButton);
            postreport = itemView.findViewById(R.id.postreport);
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
                Log.d("like ",  (data.isLike() ? "liked" : "non"));
                UserPost post = postList.get(position);
                post.setLiked(data.isLike());
                post.setLikecount(data.getCount());

                if(isFromDashboard) {
                    holder.postlikecnt.setText(String.valueOf(data.getCount()));
                    holder.postlikeButton.setImageResource(data.isLike() ? R.drawable.heart_red : R.drawable.like);
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private void openCommentDialog(String postId,String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_comments, null);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        ImageView cancelIcon = dialogView.findViewById(R.id.cmtcancelButton);
        cancelIcon.setOnClickListener(v -> dialog.dismiss());

        RecyclerView commentsRecyclerView = dialogView.findViewById(R.id.commentsRecyclerView);
        EditText commentInput = dialogView.findViewById(R.id.commentInput);
        Button sendCommentButton = dialogView.findViewById(R.id.sendCommentButton);
        noCommentsTextView = dialogView.findViewById(R.id.noCommentsTextView);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        CommentAdapter commentAdapter = new CommentAdapter(context, new ArrayList<>(),postId,userId, this::updateCommentCount);
        commentsRecyclerView.setAdapter(commentAdapter);

        // Fetch comments
        fetchComments(postId, commentAdapter,noCommentsTextView);

        sendCommentButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (commentText.isEmpty()) {
                Toast.makeText(context, "Please enter a comment before sending.", Toast.LENGTH_SHORT).show();
            } else {
                addComment(postId, commentText, commentAdapter,noCommentsTextView);
                commentInput.setText("");
            }
        });

        dialog.show();
    }
    private void fetchComments(String postId, CommentAdapter commentAdapter, TextView noCommentsTextView) {
        postCommentService.retrieveComments(postId, new DataOperationCallback<List<PostComment>>() {
            @Override
            public void onSuccess(List<PostComment> comments) {
                commentAdapter.updateComments(comments);
                int commentCount = comments.size();

                if (commentCount == 0) {
                    noCommentsTextView.setVisibility(View.VISIBLE);
                } else {
                    noCommentsTextView.setVisibility(View.GONE);
                }


                for (int i = 0; i < postList.size(); i++) {
                    UserPost post = postList.get(i);
                    if (post.getPostId().equals(postId)) {
                        post.setCommentcount(commentCount);
                        notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to load comments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addComment(String postId, String commentText,CommentAdapter commentAdapter,TextView noCommentsTextView) {
        String userId = sessionManager.getUserID();

        PostComment newComment = new PostComment(postId,userId, commentText);

        postCommentService.addPostComment(newComment, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show();
                fetchComments(postId, commentAdapter,noCommentsTextView);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to add comment", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateCommentCount(String postId) {
        for (UserPost post : postList) {
            if (post.getPostId().equals(postId)) {
                int newCommentCount = post.getCommentcount() - 1;
                post.setCommentcount(newCommentCount);
                notifyItemChanged(postList.indexOf(post));

                if (post.getCommentcount() == 0) {
                    noCommentsTextView.setVisibility(View.VISIBLE);
                } else {
                    noCommentsTextView.setVisibility(View.GONE);
                }
                break;
            }
        }
    }
    private void showLikesDialog(String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_likes, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        RecyclerView likesRecyclerView = dialogView.findViewById(R.id.likesRecyclerView);
        likesRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        List<PostLike> likesWithUsers = new ArrayList<>();
        PostLikeAdapter postLikeAdapter = new PostLikeAdapter(context,likesWithUsers);
        likesRecyclerView.setAdapter(postLikeAdapter);

        postLikeService.getLikesForPost(postId, new DataOperationCallback<List<PostLike>>() {
            @Override
            public void onSuccess(List<PostLike> likes) {
                // Assuming you have a method to convert PostLike to Users
                likesWithUsers.clear();
                likesWithUsers.addAll(likes);
                postLikeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to load likes: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void deleteLike(String postId, int position) {

    }

    private void openReportDialog(String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_report, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();


        EditText reportEditText = dialogView.findViewById(R.id.report_edit_text);
        Button reportButton = dialogView.findViewById(R.id.report_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        reportButton.setOnClickListener(v -> {
            String reportReason = reportEditText.getText().toString().trim();
            if (!reportReason.isEmpty()) {
                reportPost(postId, reportReason,dialog);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please enter a reason for reporting.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void reportPost(String postId, String reason, AlertDialog dialog) {
        Report report = new Report(sessionManager.getUserID(),postId, ReportType.POST.getReportType(), reason);
        repostService.addReport(report, new OperationCallback() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(context, "Something wrong ! Please try after some time", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

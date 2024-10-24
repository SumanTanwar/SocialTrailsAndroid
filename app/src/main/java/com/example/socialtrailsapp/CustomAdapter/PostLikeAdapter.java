package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.socialtrailsapp.FollowUnfollowActivity;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.PostLike;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.PostLikeService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.ViewProfileActivity;
import com.example.socialtrailsapp.adminpanel.AdminPostDetailActivity;
import com.example.socialtrailsapp.adminpanel.AdminUserViewActivity;

import java.util.List;

public class PostLikeAdapter extends RecyclerView.Adapter<PostLikeAdapter.PostLikeViewHolder> {

    private List<PostLike> likeList;
    private Context context;
    private SessionManager sessionManager;
    private PostLikeService postLikeService;
    public PostLikeAdapter(Context context, List<PostLike> likeList) {
        this.likeList = likeList;
        this.context = context;
        this.sessionManager = SessionManager.getInstance(context);
        this.postLikeService = new PostLikeService();
    }

    @NonNull
    @Override
    public PostLikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_likes_admin, parent, false);
        return new PostLikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostLikeViewHolder holder, int position) {
        PostLike user = likeList.get(position);

        holder.txtUserName.setText(user.getUsername());

        if (user.getProfilepicture() != null && !user.getProfilepicture().isEmpty()) {
            Uri profileImageUri = Uri.parse(user.getProfilepicture());
            Glide.with(context)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(holder.imgProfilePicture);
        } else {
            Glide.with(context)
                    .load(R.drawable.user)
                    .transform(new CircleCrop())
                    .into(holder.imgProfilePicture);
        }


        holder.txtUserName.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminUserViewActivity.class);
           if(sessionManager.getroleType().equals(UserRole.USER.getRole()))
           {
                intent = new Intent(context, FollowUnfollowActivity.class);
           }

            intent.putExtra("userId", user.getUserId());
            context.startActivity(intent);
        });

        holder.btnDeleteLike.setVisibility(sessionManager.getroleType().equals(UserRole.ADMIN.getRole()) || sessionManager.getroleType().equals(UserRole.MODERATOR.getRole()) ? View.VISIBLE : View.GONE);

        holder.btnDeleteLike.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                postLikeService.removeLike(user.getPostlikeId(), user.getPostId(), new OperationCallback() {
                    @Override
                    public void onSuccess() {
                        likeList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                        notifyItemRangeChanged(adapterPosition, likeList.size());

                        if (context instanceof AdminPostDetailActivity) {
                            ((AdminPostDetailActivity) context).onLikeDeleted(likeList.size());
                        }
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        Toast.makeText(context, "Error: " + errMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public static class PostLikeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfilePicture;
        TextView txtUserName;
        ImageButton btnDeleteLike;

        public PostLikeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfilePicture = itemView.findViewById(R.id.profileImageView);
            txtUserName = itemView.findViewById(R.id.usernameTextView);
            btnDeleteLike = itemView.findViewById(R.id.cmtdeleteButton);
        }
    }
}

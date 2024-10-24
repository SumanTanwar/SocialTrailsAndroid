package com.example.socialtrailsapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IPostLike;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.LikeResult;
import com.example.socialtrailsapp.ModelData.PostLike;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostLikeService implements IPostLike {

    private DatabaseReference reference;
    private static String _collectionName = "postlike";
    private UserPostService userPostService;
    private UserService userService;

    public PostLikeService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        userPostService = new UserPostService();
        userService = new UserService();
    }

    @Override
    public void likeandUnlikePost(String postId, String userId, DataOperationCallback<LikeResult> callback) {
        getPostLikeByUserandPostId(postId, userId, new DataOperationCallback<PostLike>() {
            @Override
            public void onSuccess(PostLike existingPostLike) {
                Log.d("post like ","like fouund " + existingPostLike.getPostId());
                reference.child(_collectionName).child(existingPostLike.getPostlikeId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            userPostService.updateLikeCount(postId, -1, new DataOperationCallback<Integer>() {
                                @Override
                                public void onSuccess(Integer count) {
                                    if (callback != null) {
                                        LikeResult result = new LikeResult(count, false); // Indicate 'unlike'
                                        callback.onSuccess(result);
                                    }

                                }

                                @Override
                                public void onFailure(String error) {
                                    if (callback != null) {
                                        callback.onFailure(error);
                                    }

                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            if (callback != null) {
                                callback.onFailure(e.getMessage());
                            }
                        });

            }

            @Override
            public void onFailure(String error) {
                // If no like exists, add a new one
                Log.d("post like ","not like fouund ");
                String newItemKey = reference.child(_collectionName).push().getKey();
                PostLike model = new PostLike(postId, userId);
                model.setPostlikeId(newItemKey);

                reference.child(_collectionName).child(newItemKey).setValue(model)
                        .addOnSuccessListener(aVoid -> {
                            userPostService.updateLikeCount(postId, 1, new DataOperationCallback<Integer>() {
                                @Override
                                public void onSuccess(Integer count) {
                                    if (callback != null) {
                                        LikeResult result = new LikeResult(count, true); // Indicate 'unlike'
                                        callback.onSuccess(result);
                                    }

                                }

                                @Override
                                public void onFailure(String error) {
                                    if (callback != null) {
                                        callback.onFailure(error);
                                    }

                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            if (callback != null) {
                                callback.onFailure(e.getMessage());
                            }
                        });

            }
        });
    }
    @Override
    public void getPostLikeByUserandPostId(String postId, String userId, DataOperationCallback<PostLike> callback) {
        Log.d("postlike","post id " + postId + " user id  " + userId);
        reference.child(_collectionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    PostLike postLike = userSnapshot.getValue(PostLike.class);

                    if (postLike != null && postLike.getUserId().equals(userId)  && postLike.getPostId().equals(postId)) {
                        Log.d("postlike","dat found like id" + postLike.getPostlikeId());
                        callback.onSuccess(postLike);
                        return;
                    }
                }
                callback.onFailure("No matching like found.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) {
                    callback.onFailure(error.getMessage());
                }
            }
        });
    }
    @Override
    public void getLikesForPost(String postId, DataOperationCallback<List<PostLike>> callback) {
        List<PostLike> likesWithUsers = new ArrayList<>();

        reference.child(_collectionName).orderByChild("postId").equalTo(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot likeSnapshot : snapshot.getChildren()) {
                    PostLike postLike = likeSnapshot.getValue(PostLike.class);
                    if (postLike != null) {
                        // Fetch user details based on userId from the postLike
                        userService.getUserByID(postLike.getUserId(), new DataOperationCallback<Users>() {
                            @Override
                            public void onSuccess(Users user) {
                                postLike.setUsername(user.getUsername());
                                postLike.setProfilepicture(user.getProfilepicture());
                                likesWithUsers.add(postLike);

                                // Check if we've processed all likes
                                if (likesWithUsers.size() == snapshot.getChildrenCount()) {
                                    callback.onSuccess(likesWithUsers);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                callback.onFailure(error);
                            }
                        });
                    }
                }
                if (likesWithUsers.isEmpty()) {
                    callback.onSuccess(likesWithUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
    @Override
    public void removeLike(String postlikeId,String postId, OperationCallback callback) {
        reference.child(_collectionName).child(postlikeId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    userPostService.updateLikeCount(postId, -1, new DataOperationCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer count) {
                            if (callback != null) {
                                // Indicate 'unlike'
                                callback.onSuccess();
                            }

                        }

                        @Override
                        public void onFailure(String error) {
                            if (callback != null) {
                                callback.onFailure(error);
                            }

                        }
                    });
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }


}

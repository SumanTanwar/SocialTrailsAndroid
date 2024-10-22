package com.example.socialtrailsapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IPostComment;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.PostComment;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostCommentService implements IPostComment {

    private DatabaseReference reference;
    private static String _collectionName = "postcomment";
    private UserService userService;

    public PostCommentService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        userService = new UserService();
    }
     @Override
    public void addPostComment(PostComment data, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        data.setPostcommentId(newItemKey);
        reference.child(_collectionName).child(newItemKey).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (callback != null) {
                            callback.onSuccess();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (callback != null) {
                            callback.onFailure(e.getMessage());
                        }
                    }
                });
    }
    @Override
    public void removePostComment(String commentId, OperationCallback callback) {
        reference.child(_collectionName).child(commentId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    @Override
    public void retrieveComments(String postId, DataOperationCallback<List<PostComment>> callback) {
        reference.child(_collectionName).orderByChild("postId").equalTo(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<PostComment> comments = new ArrayList<>();
                        int[] count = {0}; // Track completed user fetches

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            PostComment comment = snapshot.getValue(PostComment.class);
                            if (comment != null) {
                                comment.setPostcommentId(snapshot.getKey());
                                comments.add(comment);
                                userService.getUserByID(comment.getUserId(), new DataOperationCallback<Users>() {
                                    @Override
                                    public void onSuccess(Users user) {
                                        if (user != null) {
                                            comment.setUsername(user.getUsername());
                                            comment.setUserprofilepicture(user.getProfilepicture());
                                        }
                                        count[0]++;
                                        if (count[0] == comments.size()) {
                                            // Notify success with fully populated comments
                                            callback.onSuccess(comments);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        count[0]++;
                                        if (count[0] == comments.size()) {
                                            // Notify success even if user fetch failed for some
                                            callback.onSuccess(comments);
                                        }
                                    }
                                });
                            }
                        }

                        if (comments.isEmpty() && callback != null) {
                            callback.onSuccess(comments); // Handle empty comments case
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (callback != null) {
                            callback.onFailure(databaseError.getMessage());
                        }
                    }
                });
    }


}

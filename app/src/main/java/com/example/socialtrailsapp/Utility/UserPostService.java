package com.example.socialtrailsapp.Utility;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IUserPostInterface;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserPostService implements IUserPostInterface {

    private DatabaseReference reference;
    private static String _collectionName = "post";
    private PostImagesService postImagesService;
    private PostCommentService postCommentService;
    private FollowService followService;
    private UserService userService;
    public UserPostService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        postImagesService = new PostImagesService();
        postCommentService = new PostCommentService();
        followService = new FollowService();
        userService = new UserService();
    }
    @Override
    public void createPost(UserPost userPost, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        userPost.setPostId(newItemKey);
        reference.child(_collectionName).child(newItemKey).setValue(userPost.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (callback != null) {
                            postImagesService.uploadImages(newItemKey,userPost.getImageUris(),callback);
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
    public void getAllUserPost(String userId, final DataOperationCallback<List<UserPost>> callback) {
        reference.child(_collectionName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserPost> postList = new ArrayList<>();
                List<UserPost> tempList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("FirebaseData", snapshot.toString());
                    UserPost post = snapshot.getValue(UserPost.class);
                    if (post != null && userId.equals(post.getUserId())) {
                        post.setPostId(snapshot.getKey());
                        tempList.add(post);
                    }
                }


                if (tempList.isEmpty()) {
                    // Collections.sort(postList, (post1, post2) -> post2.getCreatedon().compareTo(post1.getCreatedon()));
                    callback.onSuccess(postList);
                    return;
                }

                AtomicInteger pendingRequests = new AtomicInteger(tempList.size());
                for (UserPost post : tempList) {
                    getAllPhotos(post.getPostId(), new DataOperationCallback<List<Uri>>() {
                        @Override
                        public void onSuccess(List<Uri> imageUris) {
                            post.setUploadedImageUris(imageUris);
                            postList.add(post);
                            Collections.sort(postList, (post1, post2) -> post2.getCreatedon().compareTo(post1.getCreatedon()));
                            if (pendingRequests.decrementAndGet() == 0) {
                                callback.onSuccess(postList);
                            }
                        }

                        @Override
                        public void onFailure(String error) {

                            if (pendingRequests.decrementAndGet() == 0) {
                                callback.onSuccess(postList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }
    @Override
    public void getAllUserPostDetail(String userId, final DataOperationCallback<List<UserPost>> callback) {
        reference.child(_collectionName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserPost> postList = new ArrayList<>();
                List<UserPost> tempList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserPost post = snapshot.getValue(UserPost.class);
                    if (post != null && userId.equals(post.getUserId()) && !post.getPostdeleted() && (post.getAdmindeleted() == null || !post.getAdmindeleted())) {
                        post.setPostId(snapshot.getKey());
                        tempList.add(post);
                    }
                }

                // If no posts were found, return an empty list
                if (tempList.isEmpty()) {
                    callback.onSuccess(postList);
                    return;
                }

                AtomicInteger pendingRequests = new AtomicInteger(tempList.size());

                for (UserPost post : tempList) {
                    getAllPhotos(post.getPostId(), new DataOperationCallback<List<Uri>>() {
                        @Override
                        public void onSuccess(List<Uri> imageUris) {
                            post.setUploadedImageUris(imageUris);
                            Log.d("postimage", "in user post image url " + post.getUploadedImageUris().size() );

                            retrieveUserDetails(post.getUserId(), new DataOperationCallback<Users>() {
                                @Override
                                public void onSuccess(Users userDetails) {
                                    Log.d("postimage", "success of user detail");
                                    post.setUsername(userDetails.getUsername());
                                    post.setUserprofilepicture(userDetails.getProfilepicture());

                                    countCommentsForPost(post.getPostId(), new DataOperationCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer data) {
                                            post.setCommentcount(data);
                                            Log.d("postimage", "success of comment");
                                            postList.add(post);
                                            checkCompletion(pendingRequests, postList, callback);
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            postList.add(post); // Still add the post even if comment count fails
                                            checkCompletion(pendingRequests, postList, callback);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    postList.add(post); // Still add the post even if user detail fails
                                    checkCompletion(pendingRequests, postList, callback);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            postList.add(post); // Still add the post even if photo retrieval fails
                            checkCompletion(pendingRequests, postList, callback);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    private void checkCompletion(AtomicInteger pendingRequests, List<UserPost> postList, DataOperationCallback<List<UserPost>> callback) {
        if (pendingRequests.decrementAndGet() == 0) {
            Collections.sort(postList, (post1, post2) -> post2.getCreatedon().compareTo(post1.getCreatedon()));
            Log.d("postimage", "size : " + postList.size());
            callback.onSuccess(postList);
        }
    }

    private void retrieveUserDetails(String userId, DataOperationCallback<Users> callback) {

        userService.getUserByID(userId, new DataOperationCallback<Users>() {
            @Override
            public void onSuccess(Users data) {
                callback.onSuccess(data);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure("User not found");
            }
        });


    }

    private void getAllPhotos(String postId, DataOperationCallback<List<Uri>> callback)
    {
        postImagesService.getAllPhotosByPostId(postId, new DataOperationCallback<List<Uri>>() {
            @Override
            public void onSuccess(List<Uri> imageUri) {

                if (callback != null) {
                    callback.onSuccess(imageUri);
                }
            }

            @Override
            public void onFailure(String error) {
                if (callback != null) {
                    callback.onFailure(error);
                }
            }
        });


    }
    @Override
    public void deleteUserPost(String postId,OperationCallback callback)
    {
        reference.child(_collectionName).child(postId).child("postdeleted").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
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
    public void getPostByPostId(String postId, final DataOperationCallback<UserPost> callback) {
        reference.child(_collectionName).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserPost post = dataSnapshot.getValue(UserPost.class);
                if (post != null) {
                    post.setPostId(postId);
                    getAllPhotos(post.getPostId(), new DataOperationCallback<List<Uri>>() {
                        @Override
                        public void onSuccess(List<Uri> imageUris) {
                            post.setUploadedImageUris(imageUris);
                            callback.onSuccess(post);

                        }
                        @Override
                        public void onFailure(String error) {
                            callback.onFailure(error);
                        }
                    });
                }
                else
                {
                    callback.onFailure("Post not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }
    @Override
    public void updateUserPost(UserPost post, OperationCallback callback) {
        post.setUpdatedon(Utils.getCurrentDatetime());
        reference.child(_collectionName).child(post.getPostId()).updateChildren(post.toMapUpdate())
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
    public void updateLikeCount(String postId, int change, DataOperationCallback<Integer> callback) {
        reference.child(_collectionName).child(postId).child("likecount").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentCount = mutableData.getValue(Integer.class);
                if (currentCount == null) {
                    currentCount = 0;
                }
                currentCount += change;
                mutableData.setValue(currentCount);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (error != null) {
                    if (callback != null) {
                        callback.onFailure(error.getMessage());
                    }
                } else {
                    if (callback != null) {
                        callback.onSuccess(dataSnapshot.getValue(Integer.class));
                    }
                }
            }
        });
    }
    private void countCommentsForPost(String postId, DataOperationCallback<Integer> callback) {
        postCommentService.retrieveComments(postId, new DataOperationCallback<List<PostComment>>() {
            @Override
            public void onSuccess(List<PostComment> data) {
                int commentCount = (int) data.size();
                callback.onSuccess(commentCount);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });

    }
    @Override
    public void retrievePostsForFollowedUsers(String currentUserId, final DataOperationCallback<List<UserPost>> callback) {
//        followService.getPostsFromFollowedUsers(currentUserId, new DataOperationCallback<List<String>>() {
//            @Override
//            public void onSuccess(List<String> followedUserIds) {
//
//                if (followedUserIds.isEmpty()) {
//                    Log.d("followers", "No followed users");
//                    callback.onSuccess(new ArrayList<>()); // Return an empty list
//                    return;
//                }
//
//                Log.d("followers","Follower List " + followedUserIds.get(0));
//
//                List<UserPost> postList = new ArrayList<>();
//                AtomicInteger pendingRequests = new AtomicInteger(followedUserIds.size());
//
//                for (String userId : followedUserIds) {
//                    getAllUserPostDetail(userId, new DataOperationCallback<List<UserPost>>() {
//                        @Override
//                        public void onSuccess(List<UserPost> posts) {
//                            postList.addAll(posts);
//                            if (pendingRequests.decrementAndGet() == 0) {
//                                callback.onSuccess(postList);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(String error) {
//                            if (pendingRequests.decrementAndGet() == 0) {
//                                callback.onSuccess(postList);
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(String error) {
//                callback.onFailure(error); // Handle error in fetching followed user IDs
//            }
//        });
    }

}

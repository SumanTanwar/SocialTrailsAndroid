package com.example.socialtrailsapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IUserInterface;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService implements IUserInterface {

    private DatabaseReference reference;
    private static String _collectionName = "users";

    public UserService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @Override
    public void createUser(Users user, OperationCallback callback) {
        Log.d("UserDetails", user.getAdmindeleted().toString());
        reference.child(_collectionName).child(user.getUserId()).setValue(user)
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
    public void getUserByID(String uid, DataOperationCallback<Users> callback) {
        reference.child(_collectionName).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if (user != null && !user.getAdmindeleted() && !user.getProfiledeleted()) {
                    callback.onSuccess(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public void setbackdeleteProfile(String userID) {
        Log.d("UserService", "Setback delete profile for user: " + userID);
    }

    public void deleteProfile(String userID, OperationCallback callback) {
        reference.child(_collectionName).child(userID).removeValue()
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


    public void setNotification(String userID, boolean isEnabled, OperationCallback callback) {
        reference.child(_collectionName).child(userID).child("notification").setValue(isEnabled)
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

    public void getRegularUserList(DataOperationCallback<List<Users>> callback) {
        reference.child(_collectionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Users> usersList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Users user = userSnapshot.getValue(Users.class);
                    // Check if the user is not marked as deleted
                    if (user != null && user.getRoles().equals(UserRole.USER.getRole())) {
                        usersList.add(user);
                    }
                }
                callback.onSuccess(usersList);
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
    public void suspendProfile(String userId,String suspendedBy,String reason, OperationCallback callback)
    {
        Map<String, Object> updates = new HashMap<>();
        updates.put("suspended", true);
        updates.put("suspendedby", suspendedBy);
        updates.put("suspendedreason", reason);
        updates.put("suspendedon", Utils.getCurrentDatetime());
        updates.put("isActive", false);

        reference.child(_collectionName).child(userId).updateChildren(updates)
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
    public void activateProfile(String userId,OperationCallback callback)
    {
        Map<String, Object> updates = new HashMap<>();
        updates.put("suspended", false);
        updates.put("suspendedby", null);
        updates.put("suspendedreason", null);
        updates.put("suspendedon",null);
        updates.put("isactive", true);

        reference.child(_collectionName).child(userId).updateChildren(updates)
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
    public void adminGetUserByID(String uid, DataOperationCallback<Users> callback) {
        reference.child(_collectionName).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if (user != null) {
                    callback.onSuccess(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }
    @Override
    public void adminDeleteProfile(String userId,OperationCallback callback)
    {
        Map<String, Object> updates = new HashMap<>();
        updates.put("admindeleted", true);
        updates.put("admindeletedon", Utils.getCurrentDatetime());
        updates.put("isactive", false);

        reference.child(_collectionName).child(userId).updateChildren(updates)
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
    public void adminUnDeleteProfile(String userId,OperationCallback callback)
    {
        Map<String, Object> updates = new HashMap<>();
        updates.put("admindeleted", false);
        updates.put("admindeletedon", null);
        updates.put("isactive", true);

        reference.child(_collectionName).child(userId).updateChildren(updates)
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
}


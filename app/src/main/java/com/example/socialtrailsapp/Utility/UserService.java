package com.example.socialtrailsapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.IUserInterface;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}

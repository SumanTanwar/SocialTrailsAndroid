package com.example.socialtrailsapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IIssueWarning;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.IssueWarning;
import com.example.socialtrailsapp.ModelData.Report;
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

public class IssueWarningSevice implements IIssueWarning {
    private DatabaseReference reference;
    private static String _collectionName = "issuewarning";
    private UserService userService;

    public IssueWarningSevice() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        userService = new UserService();
    }
    @Override
    public void addWarning(IssueWarning data, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        data.setIssuewarningId(newItemKey);
        reference.child(_collectionName).child(newItemKey).setValue(data.toMap())
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
    public void getWarningCount(final DataOperationCallback<Integer> callback) {
        reference.child(_collectionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                callback.onSuccess(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
    @Override
    public void fetchIssueWarnings(DataOperationCallback<List<IssueWarning>> callback) {
        reference.child(_collectionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<IssueWarning> reportsList = new ArrayList<>();

                if (snapshot.exists()) {
                    int totalReports = (int) snapshot.getChildrenCount();

                    final int[] processedReports = {0};

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        IssueWarning warning = dataSnapshot.getValue(IssueWarning.class);

                        if (warning != null) {
                            // Fetch the reporter's username
                            userService.getUserByID(warning.getIssuewarnto(), new DataOperationCallback<Users>() {
                                @Override
                                public void onSuccess(Users user) {
                                    String reporterUsername = (user != null) ? user.getUsername() : "Unknown";
                                    warning.setUsername(reporterUsername);
                                    warning.setUserprofilepicture(user.getProfilepicture());
                                    reportsList.add(warning); // Add to the list

                                    processedReports[0]++;

                                    if (processedReports[0] == totalReports) {
                                        callback.onSuccess(reportsList); // Notify success with the list
                                    }
                                }

                                @Override
                                public void onFailure(String error) {
                                    processedReports[0]++;

                                    if (processedReports[0] == totalReports) {
                                        callback.onSuccess(reportsList); // Notify success with the list
                                    }
                                }
                            });
                        } else {
                            processedReports[0]++;

                            if (processedReports[0] == totalReports) {
                                callback.onSuccess(reportsList);
                            }
                        }
                    }
                } else {
                    callback.onFailure("No warning found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure("Error fetching reports: " + error.getMessage());
            }
        });
    }
}

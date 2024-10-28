package com.example.socialtrailsapp.Utility;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IReport;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.PostComment;
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
import java.util.Map;

public class ReportService implements IReport {

    private DatabaseReference reference;
    private static String _collectionName = "report";
    private  UserService userService;
    public ReportService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        userService = new UserService();
    }
    @Override
    public void addReport(Report data, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        data.setReportid(newItemKey);
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
    public void getReportCount(final DataOperationCallback<Integer> callback) {
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
    public void fetchReports(DataOperationCallback<List<Report>> callback) {
        reference.child(_collectionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Report> reportsList = new ArrayList<>();

                if (snapshot.exists()) {
                    int totalReports = (int) snapshot.getChildrenCount();
                    final int[] processedReports = {0};

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Report report = dataSnapshot.getValue(Report.class);
                        if (report != null) {
                            // Fetch the reporter's username
                            userService.getUserByID(report.getReporterid(), new DataOperationCallback<Users>() {
                                @Override
                                public void onSuccess(Users user) {
                                    String reporterUsername = (user != null) ? user.getUsername() : "Unknown";
                                    report.setUsername(reporterUsername);
                                    report.setUserprofilepicture(user.getProfilepicture());
                                    reportsList.add(report); // Add to the list

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
                    callback.onFailure("No reports found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure("Error fetching reports: " + error.getMessage());
            }
        });
    }



}

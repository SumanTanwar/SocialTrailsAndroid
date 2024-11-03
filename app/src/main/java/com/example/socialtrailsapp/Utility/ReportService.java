package com.example.socialtrailsapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IReport;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.PostComment;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.ModelData.ReportStatus;
import com.example.socialtrailsapp.ModelData.ReportType;
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
        data.setReportId(newItemKey);
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
    @Override
    public void fetchReports(DataOperationCallback<List<Report>> callback) {
        reference.child(_collectionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Report> reportsList = new ArrayList<>();

                if (snapshot.exists()) {
                    int totalReports = (int) snapshot.getChildrenCount();
                    Log.d("report.","report count " + totalReports);
                    final int[] processedReports = {0};

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Report report = dataSnapshot.getValue(Report.class);
                        Log.d("report.","report list " + report.getReportId());
                        if (report != null) {
                            // Fetch the reporter's username
                            userService.getUserByID(report.getReporterId(), new DataOperationCallback<Users>() {
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
    @Override
    public void fetchReportByReportedId(String reportId, DataOperationCallback<Report> callback) {
        reference.child(_collectionName).orderByChild("reportId").equalTo(reportId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Report report = dataSnapshot.getValue(Report.class);
                                if (report != null) {
                                    // Now fetch the user's info using the reporterId from the report
                                    userService.getUserByID(report.getReporterId(), new DataOperationCallback<Users>() {
                                        @Override
                                        public void onSuccess(Users user) {
                                            if (user != null) {
                                                // Set the user's details into the report
                                                report.setUsername(user.getUsername());
                                                report.setUserprofilepicture(user.getProfilepicture());
                                            } else {
                                                report.setUsername("Unknown");
                                                report.setUserprofilepicture(null); // or set a default picture
                                            }
                                            // Return the report with user details
                                            callback.onSuccess(report);
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            // Handle user fetch failure
                                            report.setUsername("Unknown");
                                            report.setUserprofilepicture(null); // or set a default picture
                                            // Still return the report with fallback username
                                            callback.onSuccess(report);
                                        }
                                    });
                                    return; // Exit after finding the first report
                                }
                            }
                        } else {
                            callback.onFailure("No report found for the reportId: " + reportId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onFailure("Error fetching report: " + error.getMessage());
                    }
                });
    }
    @Override
    public void startReviewedReport(String reportId,String reviewedby, OperationCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("reviewedby", reviewedby);
        updates.put("status", ReportStatus.REVIEW.getReportStatus());

        reference.child(_collectionName).child(reportId).updateChildren(updates)
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
    public void actionTakenReport(String reportId,String actiontakenBy, OperationCallback callback) {
        Map<String, Object> updates = new HashMap<>();

        updates.put("actiontakenby", actiontakenBy);
        updates.put("status", ReportStatus.ACTIONED.getReportStatus());

        reference.child(_collectionName).child(reportId).updateChildren(updates)
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

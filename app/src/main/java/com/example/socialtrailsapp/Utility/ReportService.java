package com.example.socialtrailsapp.Utility;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.IReport;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.PostComment;
import com.example.socialtrailsapp.ModelData.Report;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportService implements IReport {

    private DatabaseReference reference;
    private static String _collectionName = "report";

    public ReportService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }
    @Override
    public void addReport(Report data, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        data.setReportid(newItemKey);
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
}
